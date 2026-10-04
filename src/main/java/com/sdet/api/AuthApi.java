package com.sdet.api;

import com.sdet.api.models.AuthRequest;
import com.sdet.config.ConfigReader;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * POST /auth - returns the token needed to update or delete a booking.
 */
public class AuthApi {

    public Response createToken(String username, String password) {
        return given()
                .spec(ApiClient.requestSpec())
                .body(new AuthRequest(username, password))
                .post("/auth");
    }

    /** Gets a valid token using the username/password from the config file. */
    public String getToken() {
        Response response = createToken(ConfigReader.get("api.username"), ConfigReader.get("api.password"));
        return response.jsonPath().getString("token");
    }
}
