package com.customize.reporter.html;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ConfigUtil.ConfigUtil;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;

import video.VideoReord;

/**
 * A Utility class which has method to create and associate a unique random number for each TestNG test method. A html
 * file will be created for each TestNG test method with this random number, which contains details about that specific
 * test method.
 * 
 */
public final class ReportDataGenerator {

    private static boolean isReportInitialized = false;

    private ReportDataGenerator() {
        // Utility class. So hide the constructor
    }

    /**
     * init the uniques id for the methods , needed to create the navigation.
     * 
     * @param suites
     */
    public static void initReportData(List<ISuite> suites) {
        if (!isReportInitialized) {
            for (ISuite suite : suites) {
                Map<String, ISuiteResult> r = suite.getResults();
                for (ISuiteResult r2 : r.values()) {
                    ITestContext tc = r2.getTestContext();
                    ITestNGMethod[] methods = tc.getAllTestMethods();
                    for (int i = 0; i < methods.length; i++) {
                        methods[i].setId(UUID.randomUUID().toString());
                        if (ConfigUtil.getConfigUtil().getConfigFileContent("isVideo").equals("true")) {
                            methods[i].setDescription(
                                    VideoReord.getInstance().getVideoPath().get(methods[i].getMethodName()));
                        }
                    }
                }
            }
            isReportInitialized = true;
        }
    }
}
