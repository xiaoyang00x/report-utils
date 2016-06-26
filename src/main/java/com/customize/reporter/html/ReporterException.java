package com.customize.reporter.html;

/**
 * This exception is to be thrown when something goes wrong while running the flow to report test case execution/result
 */
public class ReporterException extends RuntimeException {

    private static final long serialVersionUID = 8071686053553550147L;

    public ReporterException(Exception e) {
        super(e);
    }

    public ReporterException(String msg) {
        super(msg);
    }

    public ReporterException(String msg, Exception e) {
        super(msg, e);
    }

}
