package com.sdet.pages;

import com.sdet.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // Locators use the "data-test" attributes - the app adds them for testing, so they rarely change
    private final By usernameField = By.cssSelector("[data-test='username']");
    private final By passwordField = By.cssSelector("[data-test='password']");
    private final By loginButton = By.cssSelector("[data-test='login-button']");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        log("Open " + ConfigReader.get("ui.base.url"));
        driver.get(ConfigReader.get("ui.base.url"));
        // The public demo site sometimes returns a blank page when it is slow - reload it once
        if (!isLoaded()) {
            log("Login page did not load - reloading once");
            driver.navigate().refresh();
        }
        return this;
    }

    public boolean isLoaded() {
        return isVisible(loginButton);
    }

    public InventoryPage login(String username, String password) {
        log("Login with username '" + username + "'");
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return new InventoryPage(driver);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
