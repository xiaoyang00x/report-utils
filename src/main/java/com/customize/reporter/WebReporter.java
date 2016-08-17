package com.customize.reporter;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.customize.reporter.model.AbstractLog;
import com.customize.reporter.model.PageContents;
import com.customize.reporter.model.WebLog;
import com.customize.reporter.services.LogAction;

/**
 * Static log method allow you to create a more meaningful piece of log for a web test. It will log a message, but also
 * take a screenshot , get the URL of the page and capture the source of the page.
 * 
 * @see Reporter
 */
public class WebReporter extends AbstractReporter {

    @Override
    protected AbstractLog createLog(boolean takeScreenshot, boolean saveSrc) {
        String href = null;
        /**
         * Changed html file extension to txt
         */
        if (!(getSaver() instanceof SaverFileSystem)) {
            throw new RuntimeException("NI");
        }
        if (saveSrc) {
            if (getDriver() != null) {
                PageContents source = new PageContents(getDriver().getPageSource(), getBaseFileName());
                getSaver().saveSources(source);
            }
            href = "sources" + File.separator + getBaseFileName() + ".source.txt";
        }
        WebLog log = (WebLog) getLog();
        log.setHref(href);
        for (LogAction eachAction : getActionList()) {
            eachAction.perform();
        }

        return log;
    }

    protected AbstractLog createLog(boolean takeScreenshot, boolean saveSrc, boolean appiumDirver) {
        String href = null;
        /**
         * Changed html file extension to txt
         */
        if (!(getSaver() instanceof SaverFileSystem)) {
            throw new RuntimeException("NI");
        }
        if (saveSrc) {
            if (getDriver() != null) {
                PageContents source = new PageContents(getBaseFileName(), getBaseFileName());
                getSaver().saveSources(source);
            }
            href = "sources" + File.separator + getBaseFileName() + ".source.txt";
        }
        WebLog log = (WebLog) getLog();
        log.setHref(href);
        for (LogAction eachAction : getActionList()) {
            eachAction.perform();
        }

        return log;
    }

    public static void log(WebDriver driver, String message, boolean takeScreenshot, boolean saveSrc) {
        WebReporter reporter = new WebReporter();
        WebLog currentLog = new WebLog();
        reporter.setDriver(driver);
        currentLog.setMsg(message);
        currentLog.setType("WEB");
        currentLog.setLocation(Gatherer.saveGetLocation(driver));
        reporter.setLog(currentLog);
        reporter.generateLog(takeScreenshot, saveSrc);
    }

    public static void log(WebDriver driver, boolean takeScreenshot, boolean saveSrc, boolean appiumDriver) {
        WebReporter reporter = new WebReporter();
        WebLog currentLog = new WebLog();
        reporter.setDriver(driver);
        currentLog.setType("PHONE");
        reporter.setLog(currentLog);
        reporter.generateLog(takeScreenshot, saveSrc, appiumDriver, currentLog);
    }

}
