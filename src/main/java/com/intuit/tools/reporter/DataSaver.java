package com.intuit.tools.reporter;

import com.intuit.tools.reporter.model.PageContents;

/**
 * Simple interface for data persistence of web page parts.
 */
public interface DataSaver {

    /**
     * Initialize the saver.
     */
    void init();

    /**
     * Save a screenshot to the data store.
     * 
     * @param s
     *            a {@link PageContents} object
     * @return a {@link String} which represents a means for retrieving the screen shot, such as a file path or url.
     * @throws Exception
     */
    String saveScreenshot(PageContents s) throws Exception;

    /**
     * Save sources to the data store
     * 
     * @param s
     *            the {@link PageContents} object
     * @return a {@link String} which represent a means for retrieved the source code, such as a file path or url.
     */
    String saveSources(PageContents s);

    /**
     * Get a {@link PageContents} by name
     * 
     * @param name
     *            the {@link String} for retrieving the {@link PageContents} from the data store.
     * @return the retrieved {@link PageContents}
     * @throws Exception
     */
    PageContents getScreenshotByName(String name) throws Exception;
}
