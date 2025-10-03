package com.JavaEcommerce.Ecommerce.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class AuthFilters  extends OncePerRequestFilter {
    private static final Logger Logger= java.util.logging.Logger.getLogger(AuthFilters.class.getName());
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthFilters: Processing request: " + request.getRequestURI());
try{
    // Extract and validate JWT
    String jwt=jwtUtils.getJwtFromHeader(request);
    if(jwt !=null && jwtUtils.validateToken(jwt)){
        // Extract username from JWT
        String userName=jwtUtils.getUserNameFromJwt(jwt);
        // Load user details
        UserDetails userDetails=userDetailsService.loadUserByUsername(userName);
        // Create authentication token
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken (userDetails,null,userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        logger.debug("AuthFilters: User authenticated: " + userName);
    }else {
        logger.debug("AuthFilters: No JWT token found in request");
    }
}catch (Exception e){
    logger.warn("AuthFilters: Error during authentication: " + e.getMessage());
        }
        // Continue the filter chain
        filterChain.doFilter(request,response);
    }

}