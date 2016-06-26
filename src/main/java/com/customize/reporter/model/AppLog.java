package com.customize.reporter.model;

/**
 * This class represents the logs generated against a Native app on a device/simulator via a Mobile Reporter.
 * 
 */
public class AppLog extends AbstractLog {

    @Override
    protected void parse(String part) {
        // At the moment we dont have anything extra to parse apart from what AbstractLog is already parsing.
    }

    public AppLog() {
    }

    public AppLog(String s) {
        super(s);
    }

    @Override
    public boolean hasLogs() {
        return (getMsg() != null && !getMsg().trim().isEmpty());
    }
}
