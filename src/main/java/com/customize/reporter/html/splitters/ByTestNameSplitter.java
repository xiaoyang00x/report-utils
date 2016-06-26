package com.customize.reporter.html.splitters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.testng.ITestResult;

import com.customize.reporter.html.HtmlReporterListener;

/**
 * Internal use only. This class is responsible by the Velocity engine to render the "per testName" view.
 */
public final class ByTestNameSplitter extends CollectionSplitter {
    @Override
    public List<String> getKeys(ITestResult result) {
        String testName = null;
        if (result.getAttribute(HtmlReporterListener.TEST_NAME_KEY) != null) {
            testName = result.getAttribute(HtmlReporterListener.TEST_NAME_KEY).toString();
        }
        List<String> res = new ArrayList<String>();
        if (StringUtils.isNotBlank(testName)) {
            res.add(testName);
        }
        return res;
    }
}
