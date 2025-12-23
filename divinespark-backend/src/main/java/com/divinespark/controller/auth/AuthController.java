package com.divinespark.controller.auth;


import com.divinespark.dto.*;
import com.divinespark.entity.enums.OtpPurpose;
import com.divinespark.service.OtpService;
import com.divinespark.utils.JwtUtil;
import com.divinespark.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    public AuthController(AuthService authService, JwtUtil jwtUtil, OtpService otpService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(
            @Valid @RequestBody RequestOtpRequest request) {

        otpService.generateAndSendOtp(
                request.getEmail(),
                request.getPurpose()
        );

        return ResponseEntity.ok("OTP sent successfully");
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp(),
                request.getPurpose()
        );

        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity.ok(
                new AuthResponse("User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(token);
    }

}