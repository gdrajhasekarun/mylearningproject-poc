package com.home.learning.poc.cutomannotationpoc.testdata;

import org.objectweb.asm.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class TestDataInterceptorASM {

    public Map<String, List<String>> extractTestDataKeys(Class<?> clazz, Map<String, List<String>> methodTestData) throws IOException {

        for (Method method : clazz.getDeclaredMethods()) {
            List<String> testDataKeys = analyzeMethod(clazz, method.getName());
            if (!testDataKeys.isEmpty()) {
                methodTestData.put(method.getName(), testDataKeys);
            }
        }

        return methodTestData;
    }

    private List<String> analyzeMethod(Class<?> clazz, String methodName) throws IOException {
        List<String> testDataKeys = new ArrayList<>();

        ClassReader classReader = new ClassReader(clazz.getName());
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (!name.equals(methodName)) return null;

                return new MethodVisitor(Opcodes.ASM9) {
                    private Deque<String> argumentQueue = new ArrayDeque<>();

                    @Override
                    public void visitLdcInsn(Object value) {
                        if (value instanceof String) {
                            argumentQueue.add((String) value);
                        }
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if (name.equals("getData") && descriptor.equals("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")) {
                            if (argumentQueue.size() >= 2) {
                                argumentQueue.poll();
                                testDataKeys.add(argumentQueue.poll());
                            }
                        }
                    }
                };
            }
        }, 0);

        return testDataKeys;
    }

}
