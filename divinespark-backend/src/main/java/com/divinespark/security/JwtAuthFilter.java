//package com.divinespark.security;
//
//
//import com.divinespark.utils.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final CustomUserDetailsService userDetailsService;
//
//    public JwtAuthFilter(JwtUtil jwtUtil,
//                         CustomUserDetailsService userDetailsService) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//
//            String token = authHeader.substring(7);
//
//            if (jwtUtil.validateToken(token)) {
//
//                String email = jwtUtil.extractEmail(token);
//
//                // Load full CustomUserDetails
//                CustomUserDetails userDetails =
//                        (CustomUserDetails) userDetailsService
//                                .loadUserByUsername(email);
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource()
//                                .buildDetails(request)
//                );
//
//                SecurityContextHolder.getContext()
//                        .setAuthentication(authentication);
//
//                System.out.println(
//                        "SPRING PRINCIPAL → " +
//                                authentication.getPrincipal().getClass()
//                );
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//}


package com.divinespark.security;

import com.divinespark.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {

                String email = jwtUtil.extractEmail(token);

                CustomUserDetails userDetails =
                        (CustomUserDetails) userDetailsService
                                .loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }

        } catch (Exception e) {
            System.out.println("JWT error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // allow preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        return path.startsWith("/api/v1/auth/")
                || path.equals("/api/v1/auth")

                || path.startsWith("/api/v1/sessions/")
                || path.equals("/api/v1/sessions")

                || path.startsWith("/api/v1/blogs/")
                || path.startsWith("/api/v1/public/")
                || path.startsWith("/api/v1/user/review/")

                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
