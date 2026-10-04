package com.sdet.base;

import com.sdet.api.ApiClient;
import com.sdet.api.AuthApi;
import com.sdet.api.BookingApi;
import com.sdet.api.models.Booking;
import com.sdet.listeners.TestListener;
import com.sdet.utils.TestDataReader;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Listeners;

/**
 * Parent class of all API tests: gives every test the API classes and the test data.
 */
@Listeners(TestListener.class)
public class BaseApiTest {

    protected static final String API_DATA = "testdata/api-testdata.json";

    protected JsonPath testData = TestDataReader.read(API_DATA);
    protected BookingApi bookingApi = new BookingApi();
    protected AuthApi authApi = new AuthApi();

    /** Reads a booking from the test data file into a Booking POJO. */
    protected Booking getBookingData(String key) {
        return testData.getObject(key, Booking.class);
    }

    /** Creates a fresh booking for a test and returns its id, so tests never depend on each other. */
    protected int createTestBooking() {
        return bookingApi.createBooking(getBookingData("booking"))
                .then().spec(ApiClient.successSpec())
                .extract().path("bookingid");
    }
}
