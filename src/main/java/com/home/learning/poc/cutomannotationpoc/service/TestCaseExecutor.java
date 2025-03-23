package com.home.learning.poc.cutomannotationpoc.service;

import com.home.learning.poc.cutomannotationpoc.annotation.TestDataKeys;
import com.home.learning.poc.cutomannotationpoc.application.Keywords;
import com.home.learning.poc.cutomannotationpoc.model.Keyword;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataInterceptorASM;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;
import javassist.bytecode.analysis.SubroutineScanner;
import javassist.tools.reflect.Reflection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestCaseExecutor {

    TestDataProvider testDataProvider;
    TestDataInterceptorASM testDataInterceptorASM;

    public TestCaseExecutor(TestDataProvider testDataProvider, TestDataInterceptorASM testDataInterceptorASM) {
        this.testDataProvider = testDataProvider;
        this.testDataInterceptorASM = testDataInterceptorASM;
    }

    public void executeTestcases(List<String> keywordList, Map<String, String> testDataMap) throws Exception {
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage();
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
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage();
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
        Set<Class<?>> keywordClasses = getAllClassesInKeywordPackage();
        List<Keyword> keywordList = new ArrayList<>();
        for(Class<?> keywordClass: keywordClasses){
            this.testDataInterceptorASM.extractTestDataKeys(keywordClass, keywordList);
        }
        return keywordList;
    }

    private boolean isMethodFound(Class<?>keywordClass, String method){
        return Arrays.stream(keywordClass.getMethods()).filter(method1 -> method1.getName().equals(method)).findFirst().orElse(null)!=null;
    }

    private Set<Class<?>> getAllClassesInKeywordPackage() {
        Reflections reflections = new Reflections( "com.home.learning.poc.cutomannotationpoc.application", Scanners.SubTypes.filterResultsBy( s -> true));
        return reflections.get( Scanners.SubTypes.of( Object.class).asClass());
    }
}
