package com.intuit.tools.reporter.services;

/**
 * This interface facilitates custom actions to be taken place whenever a person invokes
 * {@link com.intuit.tools.reporter.WebReporter#log(WebDriver, String, boolean, boolean)} method. An instance of this interface needs to be hooked into
 * {@link com.intuit.tools.reporter.WebReporter} via a TestNG listener using the method {@link com.intuit.tools.reporter.WebReporter#addLogAction(LogAction)}.
 * 
 */
public interface LogAction {

    /**
     * Implement this method to define the custom action that needs to be done as apart of executing
     * {@link com.intuit.tools.reporter.WebReporter#log(WebDriver, String, boolean, boolean)}
     */
    void perform();

}
