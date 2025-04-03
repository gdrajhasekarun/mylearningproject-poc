package com.home.learning.poc.cutomannotationpoc.application;

import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;

public class TestKeywords {

    TestDataProvider testDataProvider;

    public TestKeywords(TestDataProvider testDataProvider) {
        this.testDataProvider = testDataProvider;
    }

    public void enterMemberDetails() {
        System.out.println(this.testDataProvider.getData("sheet1", "MemberId"));
        System.out.println(this.testDataProvider.getData("sheet1", "MemberName"));
    }
}
