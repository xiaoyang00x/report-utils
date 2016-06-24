package com.intuit.tools.reporter.html.filter;

import org.testng.ITestResult;

/**
 * This filter will be created with any of the three states ({@link ITestResult#FAILURE},{@link ITestResult#SUCCESS} AND
 * {@link ITestResult#SKIP}). It will filter those {@link ITestResult} that matches the state of the filter
 * 
 */
public class StateFilter implements Filter {

    private int state;

    public StateFilter(int state) {
        this.state = state;
    }

    @Override
    public boolean isValid(ITestResult result) {

        return result.getStatus() == this.state;
    }

}
