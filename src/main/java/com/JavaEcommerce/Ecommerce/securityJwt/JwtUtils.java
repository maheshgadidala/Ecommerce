package com.JavaEcommerce.Ecommerce.securityJwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);

@Value("${jwt.expiration.time}")
private int jwtExpirationinms;
@Value("${jwt.secret}")
private String jwtSecret;

    //getting jwt from header
    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken=request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if(bearerToken !=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7).trim();
        }
        return null;
    }
    public String genearteJwtTokenFromUserName(UserDetails userDetails){
    String userName=userDetails.getUsername();
    return Jwts.builder()
            .setSubject(userName)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date().getTime()+jwtExpirationinms)))
            .signWith(key())
            .compact();
    }
    public String getUserNameFromJwt(String token){
        if (token == null || token.isBlank()) {
            logger.debug("getUserNameFromJwt called with null/empty token");
            return null;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody().getSubject();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT: {}", e.getMessage());
            return null;
        }
    }
    public Key key(){
        try {
            // try base64 decode first
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            // not base64 - fallback to raw bytes
            logger.warn("JWT secret is not valid Base64; using raw bytes as key (not recommended)");
            byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
    public boolean validateToken(String token){
        if (token == null || token.isBlank()) {
            logger.debug("validateToken called with null/empty token");
            return false;
        }
        try{
            Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}