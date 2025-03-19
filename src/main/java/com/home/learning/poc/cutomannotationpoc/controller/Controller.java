package com.home.learning.poc.cutomannotationpoc.controller;

import com.home.learning.poc.cutomannotationpoc.model.Keyword;
import com.home.learning.poc.cutomannotationpoc.model.TestCase;
import com.home.learning.poc.cutomannotationpoc.service.TestCaseExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    TestCaseExecutor testCaseExecutor;
    public Controller(TestCaseExecutor testCaseExecutor){
        this.testCaseExecutor = testCaseExecutor;
    }

    @PostMapping("/run-testcase")
    public ResponseEntity<String> runTestCases(@RequestBody TestCase testCase) {
        String status = "Passed";
        HttpStatus httpStatus;
        try{
            this.testCaseExecutor.executeTestcases(testCase.getKeywordsList(), testCase.getTestData());
            httpStatus = HttpStatus.OK;
        }catch (Exception e){
            httpStatus = HttpStatus.BAD_REQUEST;
            status = "Failed";
        }
        return ResponseEntity.status(httpStatus).body(status);
    }

    @GetMapping("/keyword-list")
    public ResponseEntity<List<Keyword>> getAllKeywords() {
        return ResponseEntity.ok(this.testCaseExecutor.getAllKeywords());
    }
}
