
package com.customize.reporter.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.ConfigUtil.ConfigUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;

import com.customize.reporter.WebReporter;
import com.customize.reporter.html.filter.Filter;
import com.customize.reporter.html.filter.StateFilter;
import com.customize.reporter.html.splitters.ByClassSplitter;
import com.customize.reporter.html.splitters.ByGroupSplitter;
import com.customize.reporter.html.splitters.ByMethodSplitter;
import com.customize.reporter.html.splitters.ByPackageSplitter;
import com.customize.reporter.html.splitters.ByTestNameSplitter;
import com.customize.reporter.html.splitters.Line;
import com.customize.reporter.model.WebLog;

/**
 * This class is responsible for creating Html Reports using Velocity Templates. The data for the report is retrieved
 * from TestNG using the callback method {@link HtmlReporterListener#generateReport(List, List, String)}
 * 
 */
public class HtmlReporterListener implements IReporter, IInvokedMethodListener, ISuiteListener {
    /**
     * This String constant represents the JVM argument that can be enabled/disabled to enable/disable
     * {@link HtmlReporterListener}
     */
    public static final String ENABLE_HTML_REPORTER_LISTENER = "enable.html.reporter.listener";

    /**
     * This String constant holds the Key used by other classes to access the testName from the attribute map that is
     * maintained in ITestResult for the method
     */
    public static final String TEST_NAME_KEY = "testName";

    private PrintWriter out;
    private VelocityEngine ve;
    private String outputDir;

    public HtmlReporterListener() {
        // Lets register this listener with the ListenerManager
        ve = new VelocityEngine();

        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        try {
            ve.init();
        } catch (Exception e) { // catching exception because thats what is
                                // mentioned as being thrown
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuite, List<ISuite> suites, String outputDir) {

        this.outputDir = outputDir;
        ReportDataGenerator.initReportData(suites);

        out = createWriter(outputDir);
        startHtml(out);

        List<Line> lines = createSummary(suites);
        createDetail(lines);
        createMethodContent(suites, outputDir);

        endHtml(out);

        out.flush();
        out.close();
        copyResources(outputDir);
    }

    private void copyResources(String outputFolder) {
        Properties resourceListToCopy = new Properties();
        try {
            ClassLoader localClassLoader = this.getClass().getClassLoader();
            // Any new resources being added under src/main/resources/templates
            // folder
            // would need to have an entry in the Resources.properties file
            // so that it can be copied over to the testng output folder.
            resourceListToCopy.load(localClassLoader.getResourceAsStream("Resources.properties"));
            Enumeration<Object> keys = resourceListToCopy.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String fileName = resourceListToCopy.getProperty(key);
                writeStreamToFile(localClassLoader.getResourceAsStream("templates/" + fileName), fileName,
                        outputFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStreamToFile(InputStream isr, String fileName, String outputFolder) throws IOException {
        if (isr == null) {
            return;
        }
        File outFile = new File(outputFolder + File.separator + fileName);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        try (FileOutputStream outStream = new FileOutputStream(outFile)) {
            byte[] bytes = new byte[1024];
            int readLength = 0;
            while ((readLength = isr.read(bytes)) != -1) {
                outStream.write(bytes, 0, readLength);
            }
            outStream.flush();
        }
    }

    private void createDetail(List<Line> lines) {
        for (Line line : lines) {
            createContent(line);
        }
    }

    private void createContent(Line line) {
        try {
            File f = new File(outputDir + "/html/", line.getId() + ".html");
            Writer fileSystemWriter = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8")));

            Map<ITestNGMethod, List<ITestResult>> resultByMethod = new HashMap<ITestNGMethod, List<ITestResult>>();

            // find all methods
            for (ITestResult result : line.getAssociatedResults()) {
                List<ITestResult> list = resultByMethod.get(result.getMethod());
                if (list == null) {
                    list = new ArrayList<ITestResult>();
                    resultByMethod.put(result.getMethod(), list);
                }
                list.add(result);
            }

            // for each method, find all the status
            for (Entry<ITestNGMethod, List<ITestResult>> method : resultByMethod.entrySet()) {

                List<ITestResult> passed = new ArrayList<ITestResult>();
                List<ITestResult> failed = new ArrayList<ITestResult>();
                List<ITestResult> skipped = new ArrayList<ITestResult>();
                List<ITestResult> results = method.getValue();

                for (ITestResult result : results) {
                    switch (result.getStatus()) {
                    case ITestResult.SUCCESS:
                        passed.add(result);
                        break;
                    case ITestResult.FAILURE:
                        failed.add(result);
                        break;
                    case ITestResult.SKIP:
                        skipped.add(result);
                        break;
                    default:
                        throw new ReporterException(
                                "Implementation exists only for tests with status as : Success, Failure and Skipped");
                    }
                }

                // for each status // method, create the html
                if (passed.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "passed");
                    context.put("method", passed.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : passed) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }

                if (failed.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "failed");
                    context.put("method", failed.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : failed) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }
                if (skipped.size() > 0) {
                    Template t = ve.getTemplate("/templates/method.part.html");
                    VelocityContext context = new VelocityContext();
                    context.put("status", "skipped");
                    context.put("method", skipped.get(0).getMethod());
                    StringBuilder buff = new StringBuilder();
                    for (ITestResult result : skipped) {
                        buff.append(getContent(result));
                    }
                    context.put("content", buff.toString());
                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    fileSystemWriter.write(writer.toString());
                }
            }

            fileSystemWriter.flush();
            fileSystemWriter.close();
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    private void createMethodContent(List<ISuite> suites, String outdir) {
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> r = suite.getResults();
            for (ISuiteResult r2 : r.values()) {
                ITestContext ctx = r2.getTestContext();
                ITestNGMethod[] methods = ctx.getAllTestMethods();
                for (int i = 0; i < methods.length; i++) {
                    createMethod(ctx, methods[i], outdir);
                }
            }
        }
    }

    private String getContent(ITestResult result) {
        StringBuilder contentBuffer = new StringBuilder();
        contentBuffer.append(String.format("Total duration of this instance run : %02d sec. ",
                (result.getEndMillis() - result.getStartMillis()) / 1000));
        Object[] parameters = result.getParameters();
        boolean hasParameters = parameters != null && parameters.length > 0;
        List<String> msgs = Reporter.getOutput(result);
        boolean hasReporterOutput = msgs.size() > 0;
        Throwable exception = result.getThrowable();
        boolean hasThrowable = exception != null;
        List<String> imgForFilmStrip = new ArrayList<String>();
        if (hasReporterOutput || hasThrowable) {
            if (hasParameters) {
                contentBuffer.append("<h2 class='yuk_grey_midpnl_ltitle'>");
                for (int i = 0; i < parameters.length; i++) {
                    Object p = parameters[i];
                    String paramAsString = "null";
                    if (p != null) {
                        paramAsString = p.toString() + "<i>(" + p.getClass().getSimpleName() + ")</i> , ";
                    }
                    contentBuffer.append(paramAsString);
                }
                contentBuffer.append("</h2>");
            }

            if (hasReporterOutput || hasThrowable) {
                contentBuffer.append("<div class='leftContent' style='float: left; width: 70%;'>");
                contentBuffer.append("<h3>Test Log</h3>");

                for (String line : msgs) {
                    WebLog logLine = new WebLog(line);
                    if (logLine.getScreen() != null) {
                        imgForFilmStrip.add(logLine.getScreenURL());
                    }
                    String htmllog = logLine.getMsg();
                    // Attaching ralogId to each of the page title.
                    if ((logLine.getHref() != null) && (logLine.getHref().length() > 1)) {
                        htmllog = "<a href='../" + logLine.getHref() + "' title='" + logLine.getLocation() + "' >"
                                + logLine.getMsg() + "</a>";
                    }
                    // Don't output blank message w/o any Href.
                    if ((logLine.getHref() != null) || (logLine.getMsg() != null) && !logLine.getMsg().isEmpty()) {
                        contentBuffer.append(htmllog);
                        contentBuffer.append("<br/>");
                    }
                }

                if (hasThrowable) {
                    generateExceptionReport(exception, result.getMethod(), contentBuffer);
                }
            }
            contentBuffer.append("</div>"); // end of
            // leftContent

            contentBuffer.append("<div class='filmStripContainer' style='float: right; width: 100%;'>");
            contentBuffer.append("<b>Preview</b>");
            contentBuffer.append("<div class=\"filmStrip\">");
            contentBuffer.append("<ul>");
            for (String imgPath : imgForFilmStrip) {
                contentBuffer.append("<li>");
                contentBuffer.append("<a href=\"../" + imgPath + "\" > <img src=\"../" + imgPath
                        + "\" width=\"200\" height=\"200\" /> </a>");
                contentBuffer.append("</li>");
            }
            contentBuffer.append("</ul>");
            if (ConfigUtil.getConfigUtil().getConfigFileContent("isVideo").equals("true"))
                contentBuffer.append("<video src=" + Transfor4Video(result.getMethod().getDescription())
                        + " controls=\'controls\' width=\'900\' height=\'600\'></video>");
            contentBuffer.append("</div>");
            contentBuffer.append("</div>");

        }
        contentBuffer.append("<div class='clear_both'></div>");
        // Not logging the return value, because it will clog the logs
        return contentBuffer.toString();
    }

    protected void generateExceptionReport(Throwable exception, ITestNGMethod method, StringBuilder contentBuffer) {
        Throwable fortile = exception;

        String title = fortile.getMessage();
        if (title == null) {
            title = "Encountered problems when attempting to extract a meaningful Root cause.";
            if (fortile.getCause() != null && !fortile.getCause().getMessage().trim().isEmpty()) {
                title = fortile.getCause().getMessage();
            }
        }
        generateExceptionReport(exception, method, title, contentBuffer);
    }

    private void generateExceptionReport(Throwable exception, ITestNGMethod method, String title,
            StringBuilder contentBuffer) {
        generateTheStackTrace(exception, method, title, contentBuffer);
    }

    private void generateTheStackTrace(Throwable exception, ITestNGMethod method, String title,
            StringBuilder contentBuffer) {
        contentBuffer.append(" <div class='stContainer' >" + exception.getClass() + ":" + title// escape(title)
                + "<a  class='exceptionlnk'>(+)</a>");

        contentBuffer.append("<div class='exception' style='display:none'>");

        StackTraceElement[] s1 = exception.getStackTrace();
        Throwable t2 = exception.getCause();
        if ((t2 != null) && (t2.equals(exception))) {
            t2 = null;
        }

        for (int x = 0; x < s1.length; x++) {
            contentBuffer.append((x > 0 ? "<br/>at " : "") + escape(s1[x].toString()));
        }

        if (t2 != null) {
            generateExceptionReport(t2, method, "Caused by " + t2.getLocalizedMessage(), contentBuffer);
        }
        contentBuffer.append("</div></div>");
    }

    private static String escape(String string) {
        if (null == string) {
            return string;
        }
        return string.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private void createMethod(ITestContext ctx, ITestNGMethod method, String outdir) {
        try {
            File f = new File(outdir + "/html/", method.getId() + ".html");
            Writer fileSystemWriter = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8")));
            Template t = ve.getTemplate("/templates/method.part.html");

            Set<ITestResult> passed = ctx.getPassedTests().getResults(method);

            for (ITestResult result : passed) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "passed");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            Set<ITestResult> failed = ctx.getFailedTests().getResults(method);
            for (ITestResult result : failed) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "failed");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            Set<ITestResult> skipped = ctx.getSkippedTests().getResults(method);
            for (ITestResult result : skipped) {
                VelocityContext context = new VelocityContext();
                context.put("method", method);
                context.put("status", "skipped");
                context.put("result", result);
                context.put("content", getContent(result));
                StringWriter writer = new StringWriter();
                t.merge(context, writer);
                fileSystemWriter.write(writer.toString());
            }

            fileSystemWriter.flush();
            fileSystemWriter.close();
        } catch (Exception e) { // catching exception because velocity throws
                                // that and we cant change it
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    private List<Line> createSummary(List<ISuite> suites) {
        try {

            Template t = ve.getTemplate("/templates/summaryTabs.part.html");
            VelocityContext context = new VelocityContext();

            List<GroupingView> views = new ArrayList<GroupingView>();

            GroupingView view = new GroupingView("managerView", "per class", "Overview organized per class", ve, suites,
                    new ByClassSplitter());
            views.add(view);

            GroupingView view2 = new GroupingView("managerView2", "per package", "Overview organized per package", ve,
                    suites, new ByPackageSplitter());
            views.add(view2);

            GroupingView view3 = new GroupingView("managerView3", "per method", "Overview organized per method", ve,
                    suites, new ByMethodSplitter());
            views.add(view3);
            GroupingView view9 = new GroupingView("managerView9", "per testName", "Overview organized per testName", ve,
                    suites, new ByTestNameSplitter());
            views.add(view9);
            /*********************************/

            Filter f2 = new StateFilter(ITestResult.FAILURE);
            GroupingView view6 = new GroupingView("managerView6", "failed methods only",
                    "Overview organized per failed methods", ve, suites, new ByMethodSplitter(), f2);
            views.add(view6);

            GroupingView view7 = new GroupingView("managerView7", "per group", "Overview organized per group", ve,
                    suites, new ByGroupSplitter());
            views.add(view7);

            context.put("views", views);

            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());
            List<Line> lines = new ArrayList<Line>();
            for (GroupingView v : views) {
                for (Line line : v.getSplitter().getLines().values()) {
                    lines.add(line);
                }
            }
            return lines;

        } catch (Exception e) {
            ReporterException re = new ReporterException("Error occurred while generating report summary", e);
            throw re;
        }
    }

    private boolean filterConfigEntry(String key, Object value) {
        if (key.startsWith("restartWebDriver")) {
            return true;
        }
        if (key.startsWith("defaultBaseUrl")) {
            return true;
        }
        if (key.startsWith("app.")) {
            return true;
        }
        return false;
    }

    /** Starts HTML stream */
    protected void startHtml(PrintWriter out) {
        try {

            Template t = ve.getTemplate("/templates/header.part.html");
            VelocityContext context = new VelocityContext();
            StringBuilder output = new StringBuilder();
            context.put("configSummary", output.toString());
            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endHtml(PrintWriter out) {
        try {
            Template t = ve.getTemplate("/templates/footer.part.html");
            VelocityContext context = new VelocityContext();
            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            out.write(writer.toString());
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    protected PrintWriter createWriter(String outdir) {
        File f = new File(outdir + "/html/", "report.html");
        if (f.exists()) {
            Format formatter = new SimpleDateFormat("MM-dd-yyyy-HH-mm");
            String currentDate = formatter.format(new Date());
            f.renameTo(new File(outdir + "/html/", "report-" + currentDate + ".html"));
        }
        try {
            // f.getParentFile().mkdirs();
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8")));
            return pw;
        } catch (Exception e) {
            ReporterException re = new ReporterException(e);
            throw re;
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (!method.isTestMethod())
            return;
        Test TestMethod = method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class);
        Reporter.setCurrentTestResult(testResult);
        String testName = TestMethod.testName();
        String testDescription = TestMethod.description();
        String className = method.getTestMethod().getRealClass().getName();
        String methodName = method.getTestMethod().getMethodName();
        String testCaseInfo = String.format("[%s] %s#%s: %s - %s", Thread.currentThread().getId(), className,
                methodName, testName, testDescription);
        Reporter.log(testCaseInfo, true);
    }

    @Override
    public void onStart(ISuite suite) {
        String base = suite.getOutputDirectory();
        String suiteName = suite.getName();
        int index = base.lastIndexOf(suiteName);
        String rootFolder = base.substring(0, index);
        WebReporter.setTestNGOutputFolder(rootFolder);
        WebReporter.init();
    }

    @Override
    public void onFinish(ISuite suite) {
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    /**
     * 
     * @param a
     * @return
     */
    public static String Transfor4Video(String a) {
        String jobName = a.split("target")[0].split("/")[a.split("target")[0].split("/").length - 1];
        String hostName = "http://" + ConfigUtil.getConfigUtil().getConfigFileContent("JenkinsIP") + ":8080/job/";
        String finalName = hostName + jobName + "/ws/target" + a.split("target")[1];
        return finalName;
    }

}
