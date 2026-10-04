package com.sdet.api;

import com.sdet.api.models.Booking;
import com.sdet.base.BaseApiTest;
import com.sdet.listeners.RetryAnalyzer;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;

/**
 * Positive tests for the /booking endpoints: create, read, update, partial update, delete.
 * Each test creates its own booking, so tests are independent and can run in parallel.
 */
public class BookingApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"}, retryAnalyzer = RetryAnalyzer.class,
            description = "POST /booking creates a booking")
    public void createBooking() {
        Booking booking = getBookingData("booking");

        Response response = bookingApi.createBooking(booking);

        response.then()
                .spec(ApiClient.successSpec())
                .body(matchesJsonSchemaInClasspath("schemas/create-booking-schema.json"))
                .body("bookingid", greaterThan(0))
                .body("booking.firstname", equalTo(booking.getFirstname()))
                .body("booking.lastname", equalTo(booking.getLastname()))
                .body("booking.totalprice", equalTo(booking.getTotalprice()))
                .body("booking.depositpaid", equalTo(booking.isDepositpaid()))
                .body("booking.bookingdates.checkin", equalTo(booking.getBookingdates().getCheckin()))
                .body("booking.bookingdates.checkout", equalTo(booking.getBookingdates().getCheckout()));
    }

    @Test(groups = {"api", "smoke"}, retryAnalyzer = RetryAnalyzer.class,
            description = "GET /booking/{id} returns the booking")
    public void getBookingById() {
        int bookingId = createTestBooking();

        Response response = bookingApi.getBooking(bookingId);

        response.then()
                .spec(ApiClient.successSpec())
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));

        // Convert the JSON response into a POJO and check its fields
        Booking actual = response.as(Booking.class);
        Booking expected = getBookingData("booking");
        Assert.assertEquals(actual.getFirstname(), expected.getFirstname());
        Assert.assertEquals(actual.getLastname(), expected.getLastname());
        Assert.assertEquals(actual.getTotalprice(), expected.getTotalprice());
        Assert.assertEquals(actual.getAdditionalneeds(), expected.getAdditionalneeds());
    }

    @Test(groups = {"api", "regression"}, retryAnalyzer = RetryAnalyzer.class,
            description = "GET /booking returns a list of booking ids")
    public void getAllBookings() {
        bookingApi.getAllBookings()
                .then()
                .spec(ApiClient.successSpec())
                .body("bookingid", not(empty()));
    }

    @Test(groups = {"api", "regression"}, retryAnalyzer = RetryAnalyzer.class,
            description = "PUT /booking/{id} with a token updates the whole booking")
    public void updateBooking() {
        int bookingId = createTestBooking();
        Booking updated = getBookingData("updatedBooking");

        Response response = bookingApi.updateBooking(bookingId, updated, authApi.getToken());

        response.then()
                .spec(ApiClient.successSpec())
                .body("lastname", equalTo(updated.getLastname()))
                .body("totalprice", equalTo(updated.getTotalprice()))
                .body("additionalneeds", equalTo(updated.getAdditionalneeds()));

        // Read it again to prove the change was saved
        bookingApi.getBooking(bookingId)
                .then()
                .body("lastname", equalTo(updated.getLastname()));
    }

    @Test(groups = {"api", "regression"}, retryAnalyzer = RetryAnalyzer.class,
            description = "PATCH /booking/{id} changes only the fields that are sent")
    public void partialUpdateBooking() {
        int bookingId = createTestBooking();
        Map<String, Object> changes = Map.of("totalprice", 999);

        Response response = bookingApi.partialUpdateBooking(bookingId, changes, authApi.getToken());

        response.then()
                .spec(ApiClient.successSpec())
                .body("totalprice", equalTo(999))
                .body("firstname", equalTo(getBookingData("booking").getFirstname())); // not changed
    }

    @Test(groups = {"api", "regression"}, retryAnalyzer = RetryAnalyzer.class,
            description = "DELETE /booking/{id} with a token deletes the booking")
    public void deleteBooking() {
        int bookingId = createTestBooking();

        Response response = bookingApi.deleteBooking(bookingId, authApi.getToken());

        // This API returns 201 for a successful delete (see README - should be 200/204)
        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(bookingApi.getBooking(bookingId).statusCode(), 404, "Deleted booking should be gone");
    }
}
