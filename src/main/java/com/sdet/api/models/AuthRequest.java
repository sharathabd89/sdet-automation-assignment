package com.sdet.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * POJO for the POST /auth body: {"username": "...", "password": "..."}
 * NON_NULL: a null field is left out of the JSON - used to test "missing password" etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {

    private String username;
    private String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
