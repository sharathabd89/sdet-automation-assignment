package com.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Checkout step 1: customer / shipping details.
 */
public class CheckoutInformationPage extends BasePage {

    private final By firstNameField = By.cssSelector("[data-test='firstName']");
    private final By lastNameField = By.cssSelector("[data-test='lastName']");
    private final By postalCodeField = By.cssSelector("[data-test='postalCode']");
    private final By continueButton = By.cssSelector("[data-test='continue']");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(firstNameField) && driver.getCurrentUrl().contains("checkout-step-one.html");
    }

    public CheckoutOverviewPage enterDetailsAndContinue(String firstName, String lastName, String postalCode) {
        log("Enter customer details: " + firstName + " " + lastName + ", " + postalCode);
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(postalCodeField, postalCode);
        click(continueButton);
        return new CheckoutOverviewPage(driver);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
