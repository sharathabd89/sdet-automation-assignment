package com.sdet.listeners;

import com.sdet.reporting.ReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;

/**
 * TestNG calls these methods automatically. We use them to log each test and record its result in the report.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("START: {}", testName(result));
        ReportManager.createTest(testName(result), result.getMethod().getDescription());
        ReportManager.getTest().assignCategory(result.getMethod().getGroups());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("PASS: {}", testName(result));
        ReportManager.getTest().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("FAIL: {}", testName(result), result.getThrowable());
        ReportManager.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("SKIP: {}", testName(result));
        if (ReportManager.getTest() != null) {
            ReportManager.getTest().skip(result.wasRetried() ? "Failed - retried" : "Skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ReportManager.flush(); // write the HTML report
    }

    /** e.g. "LoginTest.invalidLogin [Locked out user]" - uses the "scenario" name from the test data. */
    private String testName(ITestResult result) {
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        for (Object parameter : result.getParameters()) {
            Object label = (parameter instanceof Map) ? ((Map<?, ?>) parameter).get("scenario") : parameter;
            name += " [" + label + "]";
        }
        return name;
    }
}
