package com.sdet.ui;

import com.sdet.base.BaseUiTest;
import com.sdet.config.ConfigReader;
import com.sdet.pages.InventoryPage;
import com.sdet.pages.LoginPage;
import com.sdet.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke"}, description = "Valid user logs in and sees the Products page")
    public void validLogin() {
        InventoryPage inventoryPage = loginAsValidUser();

        Assert.assertTrue(inventoryPage.isLoaded(), "Products page should be displayed");
        Assert.assertEquals(inventoryPage.getTitle(), "Products");
        Assert.assertEquals(inventoryPage.getProductCount(), 6);
        Assert.assertEquals(inventoryPage.getCartCount(), 0, "Cart should be empty after login");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return TestDataReader.getDataProvider("testdata/ui-testdata.json", "invalidLogins");
    }

    @Test(dataProvider = "invalidLogins", groups = {"ui", "negative"},
            description = "Wrong, locked or empty credentials show an error and do not log in")
    public void invalidLogin(Map<String, String> data) {
        LoginPage loginPage = new LoginPage(getDriver()).open();

        loginPage.login(data.get("username"), data.get("password"));

        Assert.assertEquals(loginPage.getErrorMessage(), data.get("expectedError"));
        Assert.assertTrue(loginPage.isLoaded(), "User should stay on the login page");
    }

    @Test(groups = {"ui", "negative"}, description = "Products page cannot be opened without logging in")
    public void productsPageNeedsLogin() {
        getDriver().get(ConfigReader.get("ui.base.url") + "/inventory.html");

        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.");
    }

    @Test(groups = {"ui", "regression"}, description = "User can log out")
    public void logout() {
        LoginPage loginPage = loginAsValidUser().logout();

        Assert.assertTrue(loginPage.isLoaded(), "Login page should be displayed after logout");
    }
}
