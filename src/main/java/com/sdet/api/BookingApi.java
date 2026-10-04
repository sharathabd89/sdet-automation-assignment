package com.sdet.api;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Reusable methods for the /booking endpoints. Tests call these instead of building requests themselves.
 * Update and delete need a token, sent as a cookie: "token=abc123".
 */
public class BookingApi {

    public Response getAllBookings() {
        return given()
                .spec(ApiClient.requestSpec())
                .get("/booking");
    }

    public Response getBooking(Object bookingId) {
        return given()
                .spec(ApiClient.requestSpec())
                .pathParam("id", bookingId)
                .get("/booking/{id}");
    }

    /** body can be a Booking object, a Map, or a plain JSON String. */
    public Response createBooking(Object body) {
        return given()
                .spec(ApiClient.requestSpec())
                .body(body)
                .post("/booking");
    }

    public Response updateBooking(int bookingId, Object body, String token) {
        return given()
                .spec(ApiClient.requestSpec())
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(body)
                .put("/booking/{id}");
    }

    public Response partialUpdateBooking(int bookingId, Object body, String token) {
        return given()
                .spec(ApiClient.requestSpec())
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(body)
                .patch("/booking/{id}");
    }

    public Response deleteBooking(int bookingId, String token) {
        return given()
                .spec(ApiClient.requestSpec())
                .cookie("token", token)
                .pathParam("id", bookingId)
                .delete("/booking/{id}");
    }
}
