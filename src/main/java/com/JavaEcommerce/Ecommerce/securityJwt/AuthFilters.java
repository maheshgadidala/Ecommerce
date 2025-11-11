package com.JavaEcommerce.Ecommerce.securityJwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilters extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilters.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip JWT validation for public endpoints - no authentication needed
        boolean shouldSkip = path != null && (
                path.startsWith("/api/auth/") ||
                        path.equals("/api/signin") ||
                        path.equals("/api/signup") ||
                        path.startsWith("/api/public/") ||
                        path.startsWith("/h2-console/") ||
                        path.equals("/error")
        );

        if (shouldSkip) {
            logger.info("AuthFilters: SKIPPING JWT validation for: {}", path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthFilters: Processing request: {}", request.getRequestURI());

        try {
            // Extract JWT from header
            String jwt = jwtUtils.getJwtFromHeader(request);

            // Only process if JWT exists
            if (jwt != null && !jwt.isEmpty()) {
                logger.debug("AuthFilters: Found JWT token, validating...");

                if (jwtUtils.validateToken(jwt)) {
                    logger.debug("AuthFilters: Token is valid, extracting username...");

                    String userName = jwtUtils.getUserNameFromJwt(jwt);

                    if (userName != null && !userName.isEmpty()) {
                        logger.debug("AuthFilters: Username extracted: {}", userName);

                        // Load user details
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                        // Create authentication token
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.debug("AuthFilters: User authenticated successfully: {}", userName);
                    } else {
                        logger.warn("AuthFilters: Could not extract username from valid token");
                    }
                } else {
                    logger.warn("AuthFilters: Token validation failed");
                }
            } else {
                logger.debug("AuthFilters: No JWT token found in request header");
            }
        } catch (Exception e) {
            logger.error("AuthFilters: Error during authentication", e);
            // Don't block the request, just log the error
        }

        // ALWAYS continue the filter chain
        filterChain.doFilter(request, response);
    }
}