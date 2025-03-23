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
        System.out.println(this.testDataProvider.getData("sheet1", "username"));
        System.out.println(this.testDataProvider.getData("sheet1", "password"));
    }

    public void enterMemberDetails() {
        System.out.println(this.testDataProvider.getData("sheet1", "MemberId"));
        System.out.println(this.testDataProvider.getData("sheet1", "MemberName"));
    }

    private void enterDetails() {
        System.out.println("Private method");
    }

    private String enterDetailsAndReturn() {
        System.out.println("Private method");
        return null;
    }
}
