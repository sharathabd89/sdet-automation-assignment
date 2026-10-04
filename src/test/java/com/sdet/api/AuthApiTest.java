package com.sdet.api;

import com.sdet.base.BaseApiTest;
import com.sdet.config.ConfigReader;
import com.sdet.listeners.RetryAnalyzer;
import com.sdet.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class AuthApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"}, retryAnalyzer = RetryAnalyzer.class,
            description = "Valid username and password return a token")
    public void createTokenWithValidCredentials() {
        Response response = authApi.createToken(ConfigReader.get("api.username"), ConfigReader.get("api.password"));

        response.then()
                .spec(ApiClient.successSpec())
                .body(matchesJsonSchemaInClasspath("schemas/auth-token-schema.json"))
                .body("token", notNullValue());
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return TestDataReader.getDataProvider(API_DATA, "invalidCredentials");
    }

    /**
     * Note: this API returns 200 + {"reason":"Bad credentials"} instead of 401 (a known API defect, see README).
     * The important check is that NO token is given.
     */
    @Test(dataProvider = "invalidCredentials", groups = {"api", "negative"}, retryAnalyzer = RetryAnalyzer.class,
            description = "Wrong or missing credentials do not return a token")
    public void createTokenWithInvalidCredentials(Map<String, String> data) {
        Response response = authApi.createToken(data.get("username"), data.get("password"));

        response.then()
                .statusCode(200)
                .body("token", nullValue())
                .body("reason", equalTo("Bad credentials"));
    }
}
