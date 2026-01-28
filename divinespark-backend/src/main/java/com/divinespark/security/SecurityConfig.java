package com.divinespark.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            OAuth2SuccessHandler oAuth2SuccessHandler
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ------------------------------------
                // CORS & CSRF
                // ------------------------------------
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // ------------------------------------
                // Stateless session (JWT)
                // ------------------------------------
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ------------------------------------
                // Authorization Rules
                // ------------------------------------
                .authorizeHttpRequests(auth -> auth

                        // Allow preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/**",
                                "/api/v1/public/**",
                                "/api/v1/payments/webhook"
                        ).permitAll()

                        // Public session browsing
                        .requestMatchers(HttpMethod.GET, "/api/v1/sessions/**").permitAll()

                        // USER APIs
                        .requestMatchers(
                                "/api/v1/user/**",
                                "/api/v1/installments/**",
                                "/api/v1/payments/**"
                        ).hasRole("USER")

                        // ADMIN APIs
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // Everything else
                        .anyRequest().authenticated()
                )

                // ------------------------------------
                // VERY IMPORTANT: Prevent OAuth Redirect for APIs
                // ------------------------------------
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
            {
              "error": "Unauthorized",
              "message": "JWT token missing or invalid"
            }
        """);
                        })
                )


                // ------------------------------------
                // OAuth ONLY for browser login
                // ------------------------------------
                .oauth2Login(oauth -> oauth
                        .loginPage("/api/v1/auth/oauth2")
                        .successHandler(oAuth2SuccessHandler)
                )

                // ------------------------------------
                // JWT Filter
                // ------------------------------------
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ------------------------------------
    // CORS Configuration
    // ------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173"   // Vite frontend
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
