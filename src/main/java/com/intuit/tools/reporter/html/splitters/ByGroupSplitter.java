package com.intuit.tools.reporter.html.splitters;

import java.util.Arrays;
import java.util.List;

import org.testng.ITestResult;
/**
 * Internal use only. This class is responsible by the Velocity engine to render the "per group" view.
 */

public final class ByGroupSplitter extends CollectionSplitter {
    @Override
    public List<String> getKeys(ITestResult result) {
        List<String> res = Arrays.asList(result.getMethod().getGroups());

        if (res.size() == 0) {
            res = Arrays.asList(new String[] { "misc" });
        }
        return res;
    }

}
