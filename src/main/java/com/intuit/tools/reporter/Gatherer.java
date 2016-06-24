package com.intuit.tools.reporter;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

class Gatherer {

    private Gatherer() {
        // Utility class. So hide the constructor
    }

    static String saveGetLocation(WebDriver driver) {
        String location = "n/a";
        try {
            if (driver != null) {
                location = driver.getCurrentUrl();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return location;
    }

    static byte[] takeScreenshot(WebDriver driver) {
        try {
            byte[] decodeBuffer = null;

            if (driver != null && driver instanceof TakesScreenshot) {
                TakesScreenshot screenshot = ((TakesScreenshot) driver);
                String ss = screenshot.getScreenshotAs(OutputType.BASE64);
                decodeBuffer = Base64.decodeBase64(ss.getBytes());
            }
            return decodeBuffer;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
