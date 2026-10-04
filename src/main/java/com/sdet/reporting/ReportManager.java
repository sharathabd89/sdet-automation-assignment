package com.sdet.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.sdet.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates the HTML report (reports/ExtentReport.html) and writes test steps to it and to the log file.
 */
public class ReportManager {

    private static final Logger logger = LogManager.getLogger(ReportManager.class);
    private static ExtentReports extent;
    // One report entry per thread, so parallel tests don't write into each other's entry
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    public static synchronized void createTest(String name, String description) {
        if (extent == null) {
            extent = new ExtentReports();
            extent.attachReporter(new ExtentSparkReporter("reports/ExtentReport.html"));
            extent.setSystemInfo("Environment", System.getProperty("env", "qa"));
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("UI URL", ConfigReader.get("ui.base.url"));
            extent.setSystemInfo("API URL", ConfigReader.get("api.base.url"));
        }
        currentTest.set(extent.createTest(name, description));
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    /** Logs a test step to the log file and the HTML report. */
    public static void log(String message) {
        logger.info(message);
        if (currentTest.get() != null) {
            currentTest.get().info(message);
        }
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
