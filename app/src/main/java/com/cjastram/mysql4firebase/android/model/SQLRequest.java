package com.cjastram.mysql4firebase.android.model;

import java.util.ArrayList;
import java.util.List;

public class SQLRequest {

    public String statementExecuted;

    public String dbStatementName;

    public String message;

    public boolean executionFailed;

    public List<String> result = new ArrayList<>();

    public List<Parameter> parameterList = new ArrayList<>();

    @Override
    public String toString() {
        return "SQLRequest{" +
                "statementExecuted='" + statementExecuted + '\'' +
                ", dbStatementName='" + dbStatementName + '\'' +
                ", message='" + message + '\'' +
                ", executionFailed=" + executionFailed +
                ", result=" + result +
                ", parameterList=" + parameterList +
                '}';
    }
}