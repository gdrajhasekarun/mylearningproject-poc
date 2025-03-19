package com.home.learning.poc.cutomannotationpoc.application;

import com.home.learning.poc.cutomannotationpoc.annotation.TestDataKeys;

import java.util.Map;

public class Keywords {


    Map<String, String> testDataMap;
    public Keywords(Map<String, String> testDataMap){
        this.testDataMap = testDataMap;
    }

    @TestDataKeys({"username", "password"})
    public void loginTest() {
        System.out.println(this.testDataMap.get("username"));
        System.out.println(this.testDataMap.get("password"));
    }
}
