package com.JavaEcommerce.Ecommerce.utils;

import java.util.List;

public class LoginResponse {

    private String token;
    private String username;
    private List<String> roles;
    public LoginResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

}
