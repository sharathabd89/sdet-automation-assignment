package com.sdet.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtils {

    /**
     * Saves a screenshot in reports/screenshots and returns its path relative to the report
     * (so the HTML report can show it).
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        String fileName = testName + "_" + System.currentTimeMillis() + ".png";
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path target = Paths.get("reports", "screenshots", fileName);
            Files.createDirectories(target.getParent());
            Files.copy(screenshot.toPath(), target);
        } catch (Exception e) {
            System.out.println("Could not take screenshot: " + e.getMessage());
        }
        return "screenshots/" + fileName;
    }
}
