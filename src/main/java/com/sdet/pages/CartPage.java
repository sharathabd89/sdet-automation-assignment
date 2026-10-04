package com.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    private final By cartList = By.cssSelector("[data-test='cart-list']");
    private final By itemNames = By.cssSelector("[data-test='inventory-item-name']");
    private final By itemPrices = By.cssSelector("[data-test='inventory-item-price']");
    private final By itemQuantities = By.cssSelector("[data-test='item-quantity']");
    private final By checkoutButton = By.cssSelector("[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(cartList) && driver.getCurrentUrl().contains("cart.html");
    }

    public List<String> getItemNames() {
        return getAllTexts(itemNames);
    }

    public List<Double> getItemPrices() {
        List<Double> prices = new ArrayList<>();
        for (String price : getAllTexts(itemPrices)) {
            prices.add(toPrice(price));
        }
        return prices;
    }

    public List<String> getItemQuantities() {
        return getAllTexts(itemQuantities);
    }

    public void removeItem(String productName) {
        log("Remove '" + productName + "' from cart");
        click(By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + productName + "']]//button"));
    }

    public CheckoutInformationPage checkout() {
        log("Click Checkout");
        click(checkoutButton);
        return new CheckoutInformationPage(driver);
    }
}
