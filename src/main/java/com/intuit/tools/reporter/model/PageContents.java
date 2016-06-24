package com.intuit.tools.reporter.model;

import java.util.Arrays;

/**
 * Object representing web page contents. Internally used. PageContents will contain two aspects of a web page:
 * <ol>
 * <li>The HTML page source
 * <li>The rendered screen image of the page
 * </ol>
 * An ID string will be associated with each of the web page aspects. User can retrieved the page contents by using this
 * ID.
 */
public final class PageContents {
    private String id;
    private String pageSource;
    private byte[] screenImage;

    /**
     * Create a new page source object.
     * 
     * @param content
     *            the content of a web page as an array of char
     * @param id
     *            the identifier to associate with this page source
     */
    public PageContents(char[] content, String id) {
        this(String.copyValueOf(content), id);
    }

    /**
     * Creates a new screen shot object
     * 
     * @param content
     *            content of the image as a byte array
     * @param id
     *            identifier associated with this image
     */
    public PageContents(byte[] content, String id) {
        this.screenImage = Arrays.copyOf(content, content.length);
        this.id = id;
    }

    /**
     * Create a new page source object.
     * 
     * @param content
     *            the content as a {@link String}
     * @param id
     *            the identifier to associate with this page source
     */
    public PageContents(String content, String id) {
        this.id = id;
        this.pageSource = content;
    }

    /**
     * Get the id for this page source object
     * 
     * @return the identifier associated as a {@link String}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the id for this page source object
     * 
     * @param id
     *            the identifier to associate as a {@link String}
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the content for this source code object
     * 
     * @return the content as a {@link String}
     */
    public String getPageSource() {
        return this.pageSource;
    }

    /**
     * Get the image content
     * 
     * @return byte array representing the image content
     */
    public byte[] getScreenImage() {
        return screenImage == null ? new byte[0] : 
        	Arrays.copyOf(screenImage, screenImage.length);
    }

    /**
     * Set the content for this source code object
     * 
     * @param content
     *            the content as a {@link String}
     */
    public void setPageSource(String content) {
        this.pageSource = content;
    }

    /**
     * Set the image content
     * 
     * @param content
     *            byte array representing the image content
     */
    public void setScreenImage(byte[] content) {
        this.screenImage = Arrays.copyOf(content, content.length);
    }

}
