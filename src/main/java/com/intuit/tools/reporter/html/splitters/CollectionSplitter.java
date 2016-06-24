package com.intuit.tools.reporter.html.splitters;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.intuit.tools.reporter.html.ReporterException;
import com.intuit.tools.reporter.html.filter.Filter;

public abstract class CollectionSplitter {

    private ISuite suite;
    private Filter filter;
    private Map<String, Line> lineById = new HashMap<String, Line>();
    private int totalInstancePassed = 0;
    private int totalInstancePassedEnvt = 0;
    private int totalInstanceFailed = 0;
    private int totalInstanceFailedEnvt = 0;
    private int totalInstanceSkipped = 0;
    private int totalInstanceSkippedEnvt = 0;

    public final void incrementTotalInstancePassedEnvt() {
        this.totalInstancePassedEnvt++;
    }

    public final void incrementTotalInstancePassed() {
        this.totalInstancePassed++;
    }

    public final void incrementTotalInstanceFailedEnvt() {
        this.totalInstanceFailedEnvt++;
    }

    public final void incrementTotalInstanceFailed() {
        this.totalInstanceFailed++;
    }

    public final void incrementTotalInstanceSkippedEnvt() {
        this.totalInstanceSkippedEnvt++;
    }

    public final void incrementTotalInstanceSkipped() {
        this.totalInstanceSkipped++;
    }

    /**
     * Return the keys the result should be associated with. For instance, for a view where the result should be ordered
     * by package, it should return the package name. It returns null is the result do not belong to any group, for
     * instance if you want a failedByPackage view, and the test is not failed. It returns a list and not unique value
     * in case the splitting is not unique. For package, class etc, the test will only have 1 key, but if you're working
     * with groups for instance, you can have one tests tagged with both SYI and seller reg and buyer reg.
     * 
     * @param result
     * @return list of keys
     */
    public abstract List<String> getKeys(ITestResult result);

    public void setSuite(ISuite suite) {
        this.suite = suite;
    }

    public void organize() {
        if (suite == null) {
            ReporterException e = new ReporterException("Bug. Suite cannot be null");
            throw e;
        }
        for (ISuiteResult suiteResult : suite.getResults().values()) {
            ITestContext ctx = suiteResult.getTestContext();
            organize(ctx.getPassedTests().getAllResults());
            organize(ctx.getFailedTests().getAllResults());
            organize(ctx.getSkippedTests().getAllResults());
        }
    }

    private void organize(Collection<ITestResult> results) {
        for (ITestResult result : results) {
            if (filter == null || filter.isValid(result)) {
                for (String key : getKeys(result)) {
                    if (key != null) {
                        Line l = lineById.get(key);
                        if (l == null) {
                            l = new Line(key, this);
                            lineById.put(key, l);
                        }
                        l.add(result);

                    }
                }
            }
        }
    }

    public Map<String, Line> getLines() {
        return lineById;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;

    }

    public int getTotalInstancePassed() {
        return totalInstancePassed;
    }

    public int getTotalInstancePassedEnvt() {
        return totalInstancePassedEnvt;
    }

    public int getTotalInstanceFailed() {
        return totalInstanceFailed;
    }

    public int getTotalInstanceFailedEnvt() {
        return totalInstanceFailedEnvt;
    }

    public int getTotalInstanceSkipped() {
        return totalInstanceSkipped;
    }

    public int getTotalInstanceSkippedEnvt() {
        return totalInstanceSkippedEnvt;
    }

}
