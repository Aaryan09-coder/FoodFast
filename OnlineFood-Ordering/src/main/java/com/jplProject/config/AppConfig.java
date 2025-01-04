package com.jplProject.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class AppConfig {

    // Configures the security filter chain for HTTP requests
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Set up authorization rules for different URL patterns
                .authorizeHttpRequests(Authorize -> Authorize
                        // Restrict access to URLs under "/api/admin/**" to users with specific roles.
                        //(e.g., "ADMIN" or "RESTAURANT_OWNER") can access them.
                        .requestMatchers("/api/admin/**").hasAnyRole("RESTAURANT_OWNER","ADMIN")
                        // Restrict access to URLs under "/api/**" so that only authenticated users can access them.
                        // This secures the general API endpoints from unauthorized access.
                        .requestMatchers("/api/**").authenticated()
                        // Allow all other URLs to be accessed without authentication.
                        // useful for public endpoints like login, registration URLs.
                        .anyRequest().permitAll()
                )
                // Add a custom filter (JwtTokenValidator) for JWT validation:
                // Adding it before the BasicAuthenticationFilter ensures that every request's token is validated before proceeding with basic authentication.
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
                // Disable CSRF protection:
                // CSRF protection is generally not required for stateless REST APIs as they don't rely on sessions.
                // JWT already provides cross-site request forgery protection since a valid token is required for each request.
                .csrf(csrf -> csrf.disable())
                // Enable Cross-Origin Resource Sharing (CORS):
                // This allows requests from specific origins (such as the frontend application) to access the backend API.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();

                // Allow only specific domains to access the backend API.
                // This is critical for security, as it prevents unauthorized websites from making requests to the API.
                cfg.setAllowedOrigins(Arrays.asList(
                        "https://zosh-food.vercel.app",
                        "https://localhost:3000"
                ));
                // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.):
                // Allows frontend applications to perform a variety of operations with the backend API.
                cfg.setAllowedMethods(Collections.singletonList("*"));
                // This setting allows cookies and authentication headers to be included in requests.
                // This is necessary for stateful aspects of CORS when working with credentials.
                cfg.setAllowCredentials(true);
                // This permits the frontend to send various types of headers (e.g., Authorization, Content-Type) without restriction.
                // This allows any custom headers that might be necessary for the application's API requests.
                cfg.setAllowedHeaders(Collections.singletonList("*"));
                // The "Authorization" header contains the JWT token, which is essential for managing the authentication state in the frontend.
                // Exposing it allows the frontend to access it and include it in subsequent requests.
                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                // Defines the maximum time (in seconds) a preflight request result can be cached.
                // This reduces the number of preflight requests by allowing caching for 3600 seconds (1 hour).
                cfg.setMaxAge(3600L);
                return cfg;
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
