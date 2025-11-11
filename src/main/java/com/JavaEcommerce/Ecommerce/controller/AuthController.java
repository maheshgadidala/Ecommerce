package com.JavaEcommerce.Ecommerce.controller;

import com.JavaEcommerce.Ecommerce.model.AppRole;
import com.JavaEcommerce.Ecommerce.model.Role;
import com.JavaEcommerce.Ecommerce.model.User;
import com.JavaEcommerce.Ecommerce.repo.RoleRepository;
import com.JavaEcommerce.Ecommerce.repo.UserRepository;
import com.JavaEcommerce.Ecommerce.request.SignupRequest;
import com.JavaEcommerce.Ecommerce.response.MessageResponse;
import com.JavaEcommerce.Ecommerce.securityJwt.JwtUtils;
import com.JavaEcommerce.Ecommerce.securityServices.UserDetailImpl;
import com.JavaEcommerce.Ecommerce.request.LoginRequest;
import com.JavaEcommerce.Ecommerce.response.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    // Authentication endpoint - supports both paths
    @PostMapping({"/api/auth/signin", "/api/signin"})
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Error: Invalid username or password"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Handle both UserDetailImpl and Spring's User
        Object principal = authentication.getPrincipal();
        String username;
        Long userId = 0L;
        List<String> roles;

        if (principal instanceof UserDetailImpl) {
            // Database user
            UserDetailImpl userDetails = (UserDetailImpl) principal;
            username = userDetails.getUsername();
            userId = userDetails.getId();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            // In-memory user
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) principal;
            username = userDetails.getUsername();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        } else {
            // Fallback
            UserDetails userDetails = (UserDetails) principal;
            username = userDetails.getUsername();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        // Generate JWT from username
        String jwt = jwtUtils.getUserNameFromJwt(username);

        LoginResponse response = new LoginResponse(userId, jwt, username, roles);
        return ResponseEntity.ok(response);
    }

    // Signup endpoint - supports both paths
    @PostMapping({"/api/auth/signup", "/api/signup"})
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

        // Check if username or email already exists
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByUserEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user entity and set fields
        User user = new User();
        user.setUserName(signupRequest.getUsername());
        user.setUserEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByRollName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRollName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRollName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRollName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}