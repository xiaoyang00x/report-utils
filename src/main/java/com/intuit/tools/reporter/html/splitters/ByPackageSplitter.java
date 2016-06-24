package com.intuit.tools.reporter.html.splitters;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;

/**
 * Internal use only. This class is responsible by the Velocity engine to render the "per package" view.
 */
public final class ByPackageSplitter extends CollectionSplitter {
    @Override
    public List<String> getKeys(ITestResult result) {
        List<String> res = new ArrayList<String>();

        Package pack = result.getMethod().getRealClass().getPackage();
        if (pack == null) {
            res.add("default package");
        } else {
            res.add(pack.getName());
        }
        return res;
    }

}
