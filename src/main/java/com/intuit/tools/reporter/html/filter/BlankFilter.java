package com.intuit.tools.reporter.html.filter;

import org.testng.ITestResult;

/**
 * A Dummy Filter implementation which doesn't filter any {@link ITestResult}
 * 
 */
public class BlankFilter implements Filter {

    @Override
    public boolean isValid(ITestResult result) {
        return true;
    }

}
