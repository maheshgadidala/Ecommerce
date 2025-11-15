package com.JavaEcommerce.Ecommerce.response;

import java.util.List;
import lombok.Data;

@Data
public class LoginResponse {

    private long id;
    private String token;
    private String username;
    private List<String> roles;

    public LoginResponse(long id, String token, String username, List<String> roles) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.roles = roles;
    }


}
