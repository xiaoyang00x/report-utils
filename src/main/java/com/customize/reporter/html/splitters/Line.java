package com.customize.reporter.html.splitters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.customize.reporter.html.ReporterException;

/**
 * Holds single unit of information for each type of {@link com.customize.reporter.html.GroupingView}.
 * 
 */
public class Line {
    private String id = null;
    private String label = "NA";

    private Set<ITestNGMethod> methods = new HashSet<ITestNGMethod>();
    private CollectionSplitter splitter;

    private int instancePassed = 0;
    private int instanceFailed = 0;
    private int instanceSkipped = 0;

    private List<ITestResult> associatedResults = new ArrayList<ITestResult>();

    public Line(String label, CollectionSplitter splitter) {
        this.splitter = splitter;
        id = UUID.randomUUID().toString();
        this.label = label;
    }

    public int getTotalMethods() {
        return methods.size();
    }

    public void add(ITestResult result) {
        associatedResults.add(result);
        methods.add(result.getMethod());

        switch (result.getStatus()) {
        case ITestResult.SUCCESS:
            instancePassed++;
            splitter.incrementTotalInstancePassed();
            break;
        case ITestResult.FAILURE:
            instanceFailed++;
            splitter.incrementTotalInstanceFailed();
            break;
        case ITestResult.SKIP:
            instanceSkipped++;
            splitter.incrementTotalInstanceSkipped();
            break;
        default:
            throw new ReporterException("Result Status is Invalid");
        }
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getInstancePassed() {
        return instancePassed;
    }

    public int getInstanceFailed() {
        return instanceFailed;
    }

    public int getInstanceSkipped() {
        return instanceSkipped;
    }

    public List<ITestResult> getAssociatedResults() {
        return associatedResults;
    }

    public CollectionSplitter getSplitter() {
        return splitter;
    }
}
