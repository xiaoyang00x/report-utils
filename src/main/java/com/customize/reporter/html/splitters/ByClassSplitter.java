package com.customize.reporter.html.splitters;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;

/**
 * Internal use only. This class is responsible by the Velocity engine to render the "per class" view.
 */
public final class ByClassSplitter extends CollectionSplitter {
    @Override
    public List<String> getKeys(ITestResult result) {
        List<String> res = new ArrayList<String>();
        res.add(result.getMethod().getRealClass().getName());
        return res;
    }

}
