package com.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Order confirmation page.
 */
public class CheckoutCompletePage extends BasePage {

    private final By header = By.cssSelector("[data-test='complete-header']");
    private final By message = By.cssSelector("[data-test='complete-text']");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(header) && driver.getCurrentUrl().contains("checkout-complete.html");
    }

    public String getHeader() {
        return getText(header);
    }

    public String getMessage() {
        return getText(message);
    }
}
