package com.home.learning.poc.cutomannotationpoc.service;

import com.home.learning.poc.cutomannotationpoc.annotation.TestDataKeys;
import com.home.learning.poc.cutomannotationpoc.model.Keyword;
import com.home.learning.poc.cutomannotationpoc.model.Steps;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataInterceptorASM;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;
//import javassist.bytecode.analysis.SubroutineScanner;
//import javassist.tools.reflect.Reflection;
//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
//import org.reflections.scanners.SubTypesScanner;
//import org.reflections.util.ClasspathHelper;
//import org.reflections.util.ConfigurationBuilder;
import com.home.learning.poc.cutomannotationpoc.testdata.TestStepInterceptorASM;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Service
public class TestCaseExecutor {

    TestDataProvider testDataProvider;
    TestDataInterceptorASM testDataInterceptorASM;
    TestStepInterceptorASM testStepInterceptorASM;

    public TestCaseExecutor(TestDataProvider testDataProvider, TestDataInterceptorASM testDataInterceptorASM, TestStepInterceptorASM testStepInterceptorASM) {
        this.testDataProvider = testDataProvider;
        this.testDataInterceptorASM = testDataInterceptorASM;
        this.testStepInterceptorASM = testStepInterceptorASM;
    }

    public void executeTestcases(List<String> keywordList, Map<String, String> testDataMap) throws Exception {
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage("com.home.learning.poc.cutomannotationpoc.application");
        for (String keyword: keywordList){
            Class<?> testClass = keywordClasses.stream().filter(keywordClass -> this.isMethodFound(keywordClass, keyword)).findFirst().orElse(null);
            if(testClass == null){
                throw new Exception("Unable to Find the keyword");
            }
            this.testDataProvider.setDataProvider(testDataMap);
            Object instance = testClass.getDeclaredConstructor(TestDataProvider.class).newInstance(testDataProvider);
            Method method = testClass.getMethod(keyword);
            method.setAccessible(true);
            method.invoke(instance);
        }
    }

    public List<Keyword> getAllKeywords() {
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage("com.home.learning.poc.cutomannotationpoc.application");
        List<Keyword> keywords = new ArrayList<>();
        for(Class<?> keywordClass: keywordClasses){
            Arrays.stream(keywordClass.getMethods())
                    .filter(method -> !method.getDeclaringClass().equals(Object.class))
                    .forEach(method -> {
                Keyword keyword = new Keyword(method.getName());
                if(method.isAnnotationPresent(TestDataKeys.class)){
                    keyword.setTestData(new HashSet<>(Arrays.asList(method.getAnnotation(TestDataKeys.class).value())));
                }
                keywords.add(keyword);
            });
        }
        return keywords;
    }

    public List<Keyword> getAllKeywordsWithoutAnnotation() throws IOException {
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage("com.home.learning.poc.cutomannotationpoc.application");
        List<Keyword> keywordList = new ArrayList<>();
        for(Class<?> keywordClass: keywordClasses){
            this.testDataInterceptorASM.extractTestDataKeys(keywordClass, keywordList);
        }
        return keywordList;
    }

    public List<Steps> getAllKeywordsWithTestSteps() throws IOException {
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage("com.home.learning.poc.cutomannotationpoc.teststeps");
        List<Steps> testSteps = new ArrayList<>();
        for(Class<?> keywordClass: keywordClasses) {
            this.testStepInterceptorASM.analyzeTestSteps(keywordClass, testSteps);
        }
        return testSteps;
    }

    private boolean isMethodFound(Class<?>keywordClass, String method){
        return Arrays.stream(keywordClass.getMethods()).filter(method1 -> method1.getName().equals(method)).findFirst().orElse(null)!=null;
    }

//    private Set<Class<?>> getAllClassesInKeywordPackage() {
//        Reflections reflections = new Reflections( "com.home.learning.poc.cutomannotationpoc.application", Scanners.SubTypes.filterResultsBy( s -> true));
//        return reflections.get( Scanners.SubTypes.of( Object.class).asClass());
//    }

    private Set<Class<?>> getAllClassesInKeywordPackage(String packageName) {
//        String packageName = "com.home.learning.poc.cutomannotationpoc.teststeps";
//        String packageName = "com.home.learning.poc.cutomannotationpoc.application";
        Set<Class<?>> classes = new HashSet<>();
        try{
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    classes.addAll(findClassesInDirectory(resource.getPath(), packageName));
                } else if (resource.getProtocol().equals("jar")) {
                    String jarPath = resource.getFile().split("!")[0].substring(5);
                    classes.addAll(findClassesInJar(jarPath, path));
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return classes;
    }

    private static List<Class<?>> findClassesInDirectory(String directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        java.io.File dir = new java.io.File(directory);
        if (!dir.exists()) return classes;

        for (String file : Objects.requireNonNull(dir.list())) {
            if (file.endsWith(".class")) {
                String className = packageName + '.' + file.substring(0, file.length() - 6);
//                if(className.endsWith("Steps"))
                    classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    private static List<Class<?>> findClassesInJar(String jarPath, String packagePath) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(packagePath) && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }
}
