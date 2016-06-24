package com.intuit.tools.reporter.html;

import java.util.List;

import org.testng.ISuite;

/**
 * This interface represents Simple View in HtmlReport.
 * 
 */
public interface View {

    /**
     * Associates the list of {@link ISuite} to the current view.
     * 
     * @param suites
     */
    void setData(List<ISuite> suites);

    /**
     * @return - A String that represents the content which is part of the current view.
     */
    String getContent();

    /**
     * @return - The id that is associated with the current view.
     */
    String getId();

    /**
     * @return - The title associated with the current view.
     */
    String getTitle();
}
