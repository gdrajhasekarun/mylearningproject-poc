package com.home.learning.poc.cutomannotationpoc.testdata;

import com.home.learning.poc.cutomannotationpoc.annotation.TestSteps;
import com.home.learning.poc.cutomannotationpoc.model.Steps;
import com.home.learning.poc.cutomannotationpoc.model.SubMethod;
import org.objectweb.asm.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class TestStepInterceptorASM {

    TestDataInterceptorASM testDataInterceptorASM;

    public TestStepInterceptorASM(TestDataInterceptorASM testDataInterceptorASM){
        this.testDataInterceptorASM = testDataInterceptorASM;
    }


    public void analyzeTestSteps(Class<?> clazz, List<Steps> stepsList) throws IOException {
        for (Method method : clazz.getDeclaredMethods()) {
            TestSteps annotation = method.getAnnotation(TestSteps.class);
            if (annotation != null) {
                Steps steps = new Steps(method.getName(), annotation.value());
                Set<String> dataKeys = analyzeMethodForDataKeys(method, clazz);
                steps.setTestData(dataKeys);
                stepsList.add(steps);
            }
        }
    }


    private Set<String> analyzeMethodForDataKeys(Method annotatedMethod, Class<?> clazz) throws IOException {
        Set<String> allDataKeys = new HashSet<>();

        // First analyze the method itself
        Set<String> directDataKeys = this.testDataInterceptorASM.analyzeMethod(clazz, annotatedMethod.getName());
        allDataKeys.addAll(directDataKeys);

        // Then analyze any methods called within this method
        List<SubMethod> calledMethodNames = this.testDataInterceptorASM.extractMethodCalls(annotatedMethod.getName(), clazz,
                "com.home.learning.poc.cutomannotationpoc.application".replace('.', '/'));
        for (SubMethod calledMethodName : calledMethodNames) {
            allDataKeys.addAll(this.testDataInterceptorASM.analyzeMethod(calledMethodName.getClassName(), calledMethodName.getMethodName()));
        }

        return allDataKeys;
    }



}
