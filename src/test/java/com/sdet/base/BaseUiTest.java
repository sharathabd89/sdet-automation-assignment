package com.sdet.base;

import com.sdet.driver.DriverManager;
import com.sdet.listeners.TestListener;
import com.sdet.pages.InventoryPage;
import com.sdet.pages.LoginPage;
import com.sdet.reporting.ReportManager;
import com.sdet.utils.ScreenshotUtils;
import com.sdet.utils.TestDataReader;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Parent class of all UI tests.
 * Before each test: open a new browser. After each test: screenshot if it failed, then close the browser.
 */
@Listeners(TestListener.class)
public class BaseUiTest {

    protected JsonPath testData = TestDataReader.read("testdata/ui-testdata.json");

    @BeforeMethod
    public void setUp() {
        DriverManager.startBrowser();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshot = ScreenshotUtils.takeScreenshot(getDriver(), result.getName());
            ReportManager.getTest().addScreenCaptureFromPath(screenshot, "Screenshot at failure");
        }
        DriverManager.quitBrowser();
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /** Most tests start logged in - this does it in one line. */
    protected InventoryPage loginAsValidUser() {
        return new LoginPage(getDriver()).open()
                .login(testData.getString("validUser.username"), testData.getString("validUser.password"));
    }
}
