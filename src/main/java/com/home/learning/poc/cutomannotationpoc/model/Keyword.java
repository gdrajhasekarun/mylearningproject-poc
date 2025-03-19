package com.home.learning.poc.cutomannotationpoc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Keyword {

    public Keyword(String keywordName) {
        this.keywordName = keywordName;
        this.testData = new ArrayList<>();
    }

    private String keywordName;
    private List<String> testData;
}
