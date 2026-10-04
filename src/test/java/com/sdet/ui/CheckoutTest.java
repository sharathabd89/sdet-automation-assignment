package com.sdet.ui;

import com.sdet.base.BaseUiTest;
import com.sdet.pages.CartPage;
import com.sdet.pages.CheckoutCompletePage;
import com.sdet.pages.CheckoutInformationPage;
import com.sdet.pages.CheckoutOverviewPage;
import com.sdet.pages.InventoryPage;
import com.sdet.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CheckoutTest extends BaseUiTest {

    /**
     * The full end-to-end flow from the assignment:
     * login -> add products -> verify cart -> checkout -> enter details -> verify summary -> finish -> verify order.
     */
    @Test(groups = {"ui", "smoke"}, description = "End-to-end purchase from login to order confirmation")
    public void completePurchase() {
        List<String> products = testData.getList("products");

        // Step 1: login
        InventoryPage inventoryPage = loginAsValidUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "Login should succeed");

        // Step 2: add products to the cart, and remember their prices
        double expectedItemTotal = 0;
        for (String product : products) {
            inventoryPage.addToCart(product);
            expectedItemTotal = expectedItemTotal + inventoryPage.getPrice(product);
        }
        Assert.assertEquals(inventoryPage.getCartCount(), products.size(), "Cart badge");

        // Step 3: verify the cart
        CartPage cartPage = inventoryPage.openCart();
        Assert.assertEquals(cartPage.getItemNames(), products, "Cart should contain the selected products");

        // Step 4: checkout and enter customer details
        CheckoutInformationPage informationPage = cartPage.checkout();
        Assert.assertTrue(informationPage.isLoaded(), "Checkout information page should open");
        CheckoutOverviewPage overviewPage = informationPage.enterDetailsAndContinue(
                testData.getString("customer.firstName"),
                testData.getString("customer.lastName"),
                testData.getString("customer.postalCode"));

        // Step 5: verify the order summary and the price calculation
        Assert.assertTrue(overviewPage.isLoaded(), "Checkout overview page should open");
        Assert.assertEquals(overviewPage.getItemNames(), products);
        Assert.assertEquals(overviewPage.getPaymentInfo(), testData.getString("order.paymentInfo"));
        Assert.assertEquals(overviewPage.getShippingInfo(), testData.getString("order.shippingInfo"));

        double expectedTax = Math.round(expectedItemTotal * testData.getDouble("order.taxRate") * 100) / 100.0;
        Assert.assertEquals(overviewPage.getItemTotal(), expectedItemTotal, 0.01, "Item total");
        Assert.assertEquals(overviewPage.getTax(), expectedTax, 0.01, "Tax");
        Assert.assertEquals(overviewPage.getTotal(), expectedItemTotal + expectedTax, 0.01, "Total");

        // Step 6: finish and verify the confirmation
        CheckoutCompletePage completePage = overviewPage.finish();
        Assert.assertTrue(completePage.isLoaded(), "Confirmation page should open");
        Assert.assertEquals(completePage.getHeader(), testData.getString("order.confirmationHeader"));
        Assert.assertEquals(completePage.getMessage(), testData.getString("order.confirmationMessage"));
        Assert.assertEquals(completePage.getCartCount(), 0, "Cart should be empty after the order");
    }

    @DataProvider(name = "invalidCustomers")
    public Object[][] invalidCustomers() {
        return TestDataReader.getDataProvider("testdata/ui-testdata.json", "invalidCustomers");
    }

    @Test(dataProvider = "invalidCustomers", groups = {"ui", "negative"},
            description = "Checkout shows an error when a required customer field is empty")
    public void checkoutWithMissingDetails(Map<String, String> data) {
        InventoryPage inventoryPage = loginAsValidUser();
        inventoryPage.addToCart(testData.getList("products").get(0).toString());
        CheckoutInformationPage informationPage = inventoryPage.openCart().checkout();

        informationPage.enterDetailsAndContinue(data.get("firstName"), data.get("lastName"), data.get("postalCode"));

        Assert.assertEquals(informationPage.getErrorMessage(), data.get("expectedError"));
        Assert.assertTrue(informationPage.isLoaded(), "User should stay on the information page");
    }
}
