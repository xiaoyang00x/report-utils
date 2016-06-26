package com.customize.reporter.html.splitters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * Internal use only. This class is responsible by the Velocity engine to render the "per method" view.
 */
public final class ByMethodSplitter extends CollectionSplitter {
    private Map<String, ITestNGMethod> methodByName = new HashMap<String, ITestNGMethod>();

    public ITestNGMethod getAssociatedMethod(String key) {
        return methodByName.get(key);
    }

    @Override
    public List<String> getKeys(ITestResult result) {
        String name = result.getMethod().getRealClass().getName() + "." + result.getMethod().getMethodName();
        methodByName.put(name, result.getMethod());
        List<String> res = new ArrayList<String>();
        res.add(name);
        return res;
    }

}
