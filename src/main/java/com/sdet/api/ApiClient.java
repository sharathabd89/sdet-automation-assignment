package com.sdet.api;

import com.sdet.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

/**
 * Common REST Assured settings, written once and reused by every API call.
 */
public class ApiClient {

    /** Request specification: base URL, JSON headers, timeout, and logging of every request/response. */
    public static RequestSpecification requestSpec() {
        int timeout = ConfigReader.getInt("api.timeout.ms");
        RestAssuredConfig timeoutConfig = RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeout)
                .setParam("http.socket.timeout", timeout));

        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .setConfig(timeoutConfig)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    /** Response specification for a successful call: status 200, JSON body, fast enough. */
    public static ResponseSpecification successSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan((long) ConfigReader.getInt("api.max.response.time.ms")))
                .build();
    }
}
