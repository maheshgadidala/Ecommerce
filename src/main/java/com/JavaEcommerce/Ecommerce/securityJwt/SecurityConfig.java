package com.JavaEcommerce.Ecommerce.securityJwt;

import com.JavaEcommerce.Ecommerce.model.AppRole;
import com.JavaEcommerce.Ecommerce.model.Role;
import com.JavaEcommerce.Ecommerce.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.HttpMethod;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService; // will be the UserDetailsServiceImpl bean

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthFilters authFilters() {
        return new AuthFilters();
    }

    // DaoAuthenticationProvider wired to the application's UserDetailsService and PasswordEncoder
    @Bean
    @Primary
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // In-memory users: user/password -> ROLE_USER, admin/mahesh@223 -> ROLE_ADMIN
    @Bean
    public UserDetailsService inMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("mahesh@223"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    // DaoAuthenticationProvider for the in-memory users
    @Bean
    public DaoAuthenticationProvider inMemoryAuthProvider(UserDetailsService inMemoryUserDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(inMemoryUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, List<AuthenticationProvider> providers) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                . sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        // allow sign-in and signup endpoints anonymously
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/**", "/api/echo").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // GET requests to public APIs require ROLE_USER (user/password)
                        .requestMatchers(HttpMethod.GET, "/api/public/**").hasRole("USER")
                        // POST requests to public APIs require ROLE_ADMIN (admin/mahesh@223)
                        .requestMatchers(HttpMethod.POST, "/api/public/**").hasRole("ADMIN")
                        // admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        // Register all AuthenticationProviders with HttpSecurity so they are used for authentication
        for (AuthenticationProvider provider : providers) {
            http.authenticationProvider(provider);
        }

        // enable HTTP Basic so the simple username/password (user/password or admin/mahesh@223) can be used
        http.httpBasic(Customizer.withDefaults());

        http.addFilterBefore(authFilters(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html"));
    }
    // Build an AuthenticationManager that uses all available AuthenticationProvider beans to avoid ambiguity
    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    // Seed roles at startup so signup can assign them
    @Bean
    public CommandLineRunner seedRoles() {
        return args -> {
            for (AppRole appRole : AppRole.values()) {
                roleRepository.findByRollName(appRole)
                        .orElseGet(() -> roleRepository.save(new Role(appRole)));
            }
        };
    }

}