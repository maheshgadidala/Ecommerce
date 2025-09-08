package com.JavaEcommerce.Ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api")
public class LoginPage {

    @GetMapping("/login")
    public String loginPage(){
        return "This is the login page";
    }
}
