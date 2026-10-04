package com.sdet.listeners;

import com.sdet.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Runs a failed test again, up to "retry.count" times (set in the config file).
 * Used on API tests only, because a public demo server can have a short network problem.
 * A real bug fails every time, so it still fails the build.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (count < ConfigReader.getInt("retry.count")) {
            count++;
            return true;
        }
        return false;
    }
}
