package com.customize.reporter.html.filter;

import org.testng.ITestResult;

/**
 * An Interface which is used to filter test method that need to display in Html Report.
 * 
 */
public interface Filter {
    boolean isValid(ITestResult result);
}
