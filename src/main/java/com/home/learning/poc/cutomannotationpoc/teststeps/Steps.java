package com.home.learning.poc.cutomannotationpoc.teststeps;

import com.home.learning.poc.cutomannotationpoc.annotation.TestSteps;
import com.home.learning.poc.cutomannotationpoc.application.Keywords;
import com.home.learning.poc.cutomannotationpoc.application.TestKeywords;
import com.home.learning.poc.cutomannotationpoc.testdata.TestDataProvider;

public class Steps {


    TestDataProvider testDataProvider;
    public Steps(TestDataProvider testDataProvider){
        this.testDataProvider = testDataProvider;
    }

    @TestSteps("login to Application")
    public void loginToApplication() {
        System.out.println("Logged in");
        new Keywords(this.testDataProvider).loginTest();
        new TestKeywords(this.testDataProvider).enterMemberDetails();
    }


}
