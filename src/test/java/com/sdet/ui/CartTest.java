package com.sdet.ui;

import com.sdet.base.BaseUiTest;
import com.sdet.pages.CartPage;
import com.sdet.pages.InventoryPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CartTest extends BaseUiTest {

    @Test(groups = {"ui", "regression"}, description = "Cart shows the added products with correct name, price and quantity")
    public void cartShowsAddedProducts() {
        List<String> products = testData.getList("products");
        InventoryPage inventoryPage = loginAsValidUser();
        double firstPrice = inventoryPage.getPrice(products.get(0));
        double secondPrice = inventoryPage.getPrice(products.get(1));

        inventoryPage.addToCart(products.get(0));
        inventoryPage.addToCart(products.get(1));
        Assert.assertEquals(inventoryPage.getCartCount(), 2, "Cart badge");

        CartPage cartPage = inventoryPage.openCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should open");
        Assert.assertEquals(cartPage.getItemNames(), products);
        Assert.assertEquals(cartPage.getItemPrices(), List.of(firstPrice, secondPrice));
        Assert.assertEquals(cartPage.getItemQuantities(), List.of("1", "1"));
    }

    @Test(groups = {"ui", "regression"}, description = "Removing a product from the cart updates the cart and badge")
    public void removeProductFromCart() {
        List<String> products = testData.getList("products");
        InventoryPage inventoryPage = loginAsValidUser();
        inventoryPage.addToCart(products.get(0));
        inventoryPage.addToCart(products.get(1));

        CartPage cartPage = inventoryPage.openCart();
        cartPage.removeItem(products.get(1));

        Assert.assertEquals(cartPage.getItemNames(), List.of(products.get(0)));
        Assert.assertEquals(cartPage.getCartCount(), 1);
    }

    @Test(groups = {"ui", "regression"}, description = "Add button changes to Remove and back; badge disappears at 0")
    public void addAndRemoveOnProductsPage() {
        String product = testData.getList("products").get(0).toString();
        InventoryPage inventoryPage = loginAsValidUser();

        inventoryPage.addToCart(product);
        Assert.assertEquals(inventoryPage.getButtonText(product), "Remove");
        Assert.assertEquals(inventoryPage.getCartCount(), 1);

        inventoryPage.removeFromCart(product);
        Assert.assertEquals(inventoryPage.getButtonText(product), "Add to cart");
        Assert.assertEquals(inventoryPage.getCartCount(), 0);
    }
}
