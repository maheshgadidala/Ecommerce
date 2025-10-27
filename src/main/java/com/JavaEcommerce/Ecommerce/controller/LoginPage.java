package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.utils.JwtUtils;
import com.JavaEcommerce.Ecommerce.utils.LoginRequest;
import com.JavaEcommerce.Ecommerce.utils.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LoginPage {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;
    //Authentication end point
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request){
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new RuntimeException(e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userdetails= (UserDetails) authentication.getPrincipal();
        String jwt=jwtUtils.genearteJwtTokenFromUserName(userdetails);
        List<String> roles=userdetails.getAuthorities().stream()
                .map(item->item.getAuthority())
                .toList();
        LoginResponse loginResponse=new LoginResponse(jwt,
                userdetails.getUsername(),
                roles);
        return ResponseEntity.ok(loginResponse);
    }

}
