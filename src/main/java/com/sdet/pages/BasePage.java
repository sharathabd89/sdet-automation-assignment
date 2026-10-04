package com.sdet.pages;

import com.sdet.config.ConfigReader;
import com.sdet.reporting.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Parent class of all pages. Holds the common actions so every page can reuse them.
 * Every action first WAITS for the element (explicit wait) - no Thread.sleep anywhere.
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Header elements, shown on every page after login
    private final By cartIcon = By.cssSelector("[data-test='shopping-cart-link']");
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
    }

    // ---------- reusable actions ----------

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected List<String> getAllTexts(By locator) {
        List<String> texts = new ArrayList<>();
        for (WebElement element : driver.findElements(locator)) {
            texts.add(element.getText());
        }
        return texts;
    }

    /** Waits for the element; returns false (instead of failing) if it does not appear. */
    protected boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** "$29.99" or "Total: $43.18" -> 29.99 / 43.18 */
    protected double toPrice(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }

    protected void log(String step) {
        ReportManager.log(step);
    }

    // ---------- header actions (available on every logged-in page) ----------

    /** The badge is removed when the cart is empty, so no badge means 0. */
    public int getCartCount() {
        List<WebElement> badge = driver.findElements(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    public CartPage openCart() {
        log("Open cart");
        click(cartIcon);
        return new CartPage(driver);
    }
}
