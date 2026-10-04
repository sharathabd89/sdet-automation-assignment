package com.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * "Products" page - shown after login.
 */
public class InventoryPage extends BasePage {

    private final By pageTitle = By.cssSelector("[data-test='title']");
    private final By productNames = By.cssSelector("[data-test='inventory-item-name']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.cssSelector("[data-test='logout-sidebar-link']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(pageTitle) && driver.getCurrentUrl().contains("inventory.html");
    }

    public String getTitle() {
        return getText(pageTitle);
    }

    public int getProductCount() {
        return driver.findElements(productNames).size();
    }

    // Each product is a card. We find the card by product name, then the button / price inside it.
    private By addOrRemoveButton(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + productName + "']]//button");
    }

    private By price(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + productName + "']]"
                + "//div[@data-test='inventory-item-price']");
    }

    public void addToCart(String productName) {
        log("Add '" + productName + "' to cart");
        click(addOrRemoveButton(productName));
    }

    public void removeFromCart(String productName) {
        log("Remove '" + productName + "' from cart");
        click(addOrRemoveButton(productName));
    }

    public String getButtonText(String productName) {
        return getText(addOrRemoveButton(productName));
    }

    public double getPrice(String productName) {
        return toPrice(getText(price(productName)));
    }

    public LoginPage logout() {
        log("Logout");
        click(menuButton);
        click(logoutLink);
        return new LoginPage(driver);
    }
}
