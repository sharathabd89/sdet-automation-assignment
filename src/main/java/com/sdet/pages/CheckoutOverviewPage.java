package com.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Checkout step 2: order summary with items, payment / shipping info and prices.
 */
public class CheckoutOverviewPage extends BasePage {

    private final By itemNames = By.cssSelector("[data-test='inventory-item-name']");
    private final By paymentInfo = By.cssSelector("[data-test='payment-info-value']");
    private final By shippingInfo = By.cssSelector("[data-test='shipping-info-value']");
    private final By itemTotal = By.cssSelector("[data-test='subtotal-label']");
    private final By tax = By.cssSelector("[data-test='tax-label']");
    private final By total = By.cssSelector("[data-test='total-label']");
    private final By finishButton = By.cssSelector("[data-test='finish']");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(finishButton) && driver.getCurrentUrl().contains("checkout-step-two.html");
    }

    public List<String> getItemNames() {
        return getAllTexts(itemNames);
    }

    public String getPaymentInfo() {
        return getText(paymentInfo);
    }

    public String getShippingInfo() {
        return getText(shippingInfo);
    }

    public double getItemTotal() {
        return toPrice(getText(itemTotal));
    }

    public double getTax() {
        return toPrice(getText(tax));
    }

    public double getTotal() {
        return toPrice(getText(total));
    }

    public CheckoutCompletePage finish() {
        log("Click Finish");
        click(finishButton);
        return new CheckoutCompletePage(driver);
    }
}
