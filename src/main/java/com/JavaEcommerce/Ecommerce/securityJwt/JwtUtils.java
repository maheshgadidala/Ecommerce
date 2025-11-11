package com.JavaEcommerce.Ecommerce.securityJwt;

import com.JavaEcommerce.Ecommerce.securityServices.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.expiration.time}")
    private long jwtExpirationMs;

    @Value("${jwt.secret}")
    private String jwtSecret;

            @Value("${jwt.cookie.name}")
    private String jwtCookie;

//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            String token = bearerToken.substring(7).trim();
//            logger.debug("Extracted JWT from header, length: {}", token.length());
//            return token;
//        }
//        logger.debug("No Bearer token found in Authorization header");
//        return null;
//    }

    //JwtCookies
    public String getJwtFromCookies(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        if (cookie != null) {
            String token = cookie.getValue();
            logger.debug("Extracted JWT from cookie, length: {}", token.length());
            return token;
        } else {
            logger.debug("No JWT cookie found");
            return null;
        }

    }

    //Generate JwtCookie
    public ResponseCookie generateJwtCookie(UserDetailImpl userDetails){
        String jwt = generateJwtTokenFromUsername(userDetails.getUsername());
        logger.debug("Generated JWT for cookie, length: {}", jwt.length());
        return ResponseCookie.from(jwtCookie,jwt)
                .path("/api")
                .maxAge(jwtExpirationMs/1000)
                .httpOnly(false)
                .build();
    }



    public String generateJwtTokenFromUsername(String username) {
        logger.info("=== Generating JWT ===");
        logger.info("Username: {}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty!");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        try {
            Key key = getSigningKey();

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

            logger.info("Issued at: {}", now);
            logger.info("Expires at: {}", expiryDate);

            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            if (token == null || token.isEmpty()) {
                logger.error("Generated token is null or empty!");
                throw new RuntimeException("Token generation failed");
            }

            logger.info("Token generated successfully!");
            logger.info("Token length: {}", token.length());
            logger.info("Token preview: {}...", token.substring(0, Math.min(50, token.length())));
            logger.info("=====================");

            return token;
        } catch (Exception e) {
            logger.error("ERROR GENERATING TOKEN:", e);
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    public String getUserNameFromJwt(String token) {
        // Check for null or empty BEFORE trying to parse
        if (token == null) {
            logger.warn("getUserNameFromJwt: token is null");
            return null;
        }

        token = token.trim();

        if (token.isEmpty()) {
            logger.warn("getUserNameFromJwt: token is empty after trim");
            return null;
        }

        // Check if token has the right format (3 parts separated by dots)
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            logger.error("getUserNameFromJwt: Invalid JWT format. Expected 3 parts, got: {}. Token: '{}'", parts.length, token);
            return null;
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            logger.debug("Extracted username from JWT: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            logger.error("Failed to parse JWT: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error parsing JWT", e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        // Check for null or empty BEFORE trying to validate
        if (token == null) {
            logger.debug("validateToken: token is null");
            return false;
        }

        token = token.trim();

        if (token.isEmpty()) {
            logger.debug("validateToken: token is empty after trim");
            return false;
        }

        // Check if token has the right format
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            logger.warn("validateToken: Invalid JWT format. Expected 3 parts, got: {}", parts.length);
            return false;
        }

        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token (malformed): {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation", e);
        }
        return false;
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            logger.debug("Decoded Base64 secret, length: {} bytes", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.warn("Failed to decode Base64 secret, using raw bytes");
            byte[] keyBytes = jwtSecret.getBytes();
            if (keyBytes.length < 64) {
                throw new IllegalArgumentException("JWT secret must be at least 64 bytes for HS512. Current length: " + keyBytes.length);
            }
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}