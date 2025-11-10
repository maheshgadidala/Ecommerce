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

public class AuthFilters  extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilters.class);
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String servletPath = request.getServletPath();

        // Skip JWT validation for these endpoints (check both servletPath and requestURI)
        boolean shouldSkip = (path != null && (path.equals("/api/signin") || path.equals("/api/echo") || path.startsWith("/api/public/") || path.startsWith("/api/auth/") || path.startsWith("/h2-console/")))
                || (servletPath != null && (servletPath.equals("/api/signin") || servletPath.equals("/api/echo") || servletPath.startsWith("/api/public/") || servletPath.startsWith("/api/auth/") || servletPath.startsWith("/h2-console/")));

        if (shouldSkip) {
            logger.info("AuthFilters: Skipping JWT validation for: " + path + " (servletPath=" + servletPath + ")");
        }

        return shouldSkip;
    }
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
                if (userName != null) {
                    // Load user details
                    UserDetails userDetails=userDetailsService.loadUserByUsername(userName);
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken (userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("AuthFilters: User authenticated: " + userName);
                } else {
                    logger.debug("AuthFilters: Token parsed but username was null");
                }
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