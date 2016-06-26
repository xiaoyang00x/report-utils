package com.customize.reporter.services;

/**
 * This interface facilitates custom actions to be taken place whenever a person invokes
 * {@link com.customize.reporter.WebReporter#log(WebDriver, String, boolean, boolean)} method. An instance of this interface needs to be hooked into
 * {@link com.customize.reporter.WebReporter} via a TestNG listener using the method {@link com.customize.reporter.WebReporter#addLogAction(LogAction)}.
 * 
 */
public interface LogAction {

    /**
     * Implement this method to define the custom action that needs to be done as apart of executing
     * {@link com.customize.reporter.WebReporter#log(WebDriver, String, boolean, boolean)}
     */
    void perform();

}
