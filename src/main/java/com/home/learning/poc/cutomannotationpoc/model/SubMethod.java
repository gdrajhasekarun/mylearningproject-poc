package com.home.learning.poc.cutomannotationpoc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubMethod {

    private Class<?> className;
    private String methodName;
}
