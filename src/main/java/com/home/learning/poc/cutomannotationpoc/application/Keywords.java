package com.home.learning.poc.cutomannotationpoc.application;

import com.home.learning.poc.cutomannotationpoc.annotation.TestDataKeys;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;

public class Keywords {

    TestDataProvider testDataProvider;

    public Keywords(TestDataProvider testDataProvider) {
        this.testDataProvider = testDataProvider;
    }

    @TestDataKeys({"username", "password"})
    public void loginTest() {
        System.out.println(this.testDataProvider.getData("sheet1", "username"));
        System.out.println(this.testDataProvider.getData("sheet1", "password"));
    }


    private void enterDetails() {
        System.out.println("Private method");
    }

    private String enterDetailsAndReturn() {
        System.out.println("Private method");
        return null;
    }

}
