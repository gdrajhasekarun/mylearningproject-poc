package com.home.learning.poc.cutomannotationpoc.application;

import com.home.learning.poc.cutomannotationpoc.annotation.TestDataKeys;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

public class Keywords {


    TestDataProvider testDataProvider;
    public Keywords(TestDataProvider testDataProvider){
        this.testDataProvider = testDataProvider;
    }

    @TestDataKeys({"username", "password"})
    public void loginTest() {
        System.out.println(this.testDataProvider.getData("", "username"));
        System.out.println(this.testDataProvider.getData("", "password"));
    }
}
