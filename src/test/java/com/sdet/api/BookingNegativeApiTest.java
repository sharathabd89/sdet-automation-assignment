package com.sdet.api;

import com.sdet.base.BaseApiTest;
import com.sdet.listeners.RetryAnalyzer;
import com.sdet.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Negative tests: invalid id, invalid token, missing fields, invalid JSON.
 */
public class BookingNegativeApiTest extends BaseApiTest {

    @DataProvider(name = "invalidIds")
    public Object[][] invalidIds() {
        return new Object[][]{{999999999}, {0}, {-1}, {"abc"}};
    }

    @Test(dataProvider = "invalidIds", groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "GET with an id that does not exist returns 404")
    public void getBookingWithInvalidId(Object invalidId) {
        Response response = bookingApi.getBooking(invalidId);

        Assert.assertEquals(response.statusCode(), 404);
    }

    @DataProvider(name = "invalidTokens")
    public Object[][] invalidTokens() {
        return new Object[][]{{"wrong-token-123"}, {""}};
    }

    @Test(dataProvider = "invalidTokens", groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "PUT with an invalid token is forbidden (403)")
    public void updateWithInvalidToken(String token) {
        int bookingId = createTestBooking();

        Response response = bookingApi.updateBooking(bookingId, getBookingData("updatedBooking"), token);

        Assert.assertEquals(response.statusCode(), 403);
    }

    @Test(dataProvider = "invalidTokens", groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "DELETE with an invalid token is forbidden (403) and the booking still exists")
    public void deleteWithInvalidToken(String token) {
        int bookingId = createTestBooking();

        Response response = bookingApi.deleteBooking(bookingId, token);

        Assert.assertEquals(response.statusCode(), 403);
        Assert.assertEquals(bookingApi.getBooking(bookingId).statusCode(), 200, "Booking should not be deleted");
    }

    @DataProvider(name = "incompleteBookings")
    public Object[][] incompleteBookings() {
        return TestDataReader.getDataProvider(API_DATA, "incompleteBookings");
    }

    /**
     * Note: this API returns 500 instead of 400 when required fields are missing (a known API defect, see README).
     */
    @Test(dataProvider = "incompleteBookings", groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "POST with missing required fields is rejected")
    public void createBookingWithMissingFields(Map<String, Object> data) {
        Response response = bookingApi.createBooking(data.get("body"));

        Assert.assertEquals(response.statusCode(), 500);
    }

    @Test(groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "POST with broken JSON returns 400 Bad Request")
    public void createBookingWithInvalidJson() {
        Response response = bookingApi.createBooking("{\"firstname\": \"John\", ");

        Assert.assertEquals(response.statusCode(), 400);
    }
}
