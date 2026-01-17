package com.divinespark.controller.auth;

import com.divinespark.dto.*;
import com.divinespark.entity.enums.OtpPurpose;
import com.divinespark.service.AuthService;
import com.divinespark.service.OtpService;
import com.divinespark.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                          OtpService otpService,
                          JwtUtil jwtUtil) {
        this.authService = authService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(
            @Valid @RequestBody RequestOtpRequest request) {

        otpService.generateAndSendOtp(
                request.getEmail(), request.getPurpose());

        return ResponseEntity.ok(
                Map.of("message", "OTP sent"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp(),
                request.getPurpose()
        );

        if (request.getPurpose() == OtpPurpose.VERIFY_EMAIL) {
            String token =
                    jwtUtil.generateToken(
                            request.getEmail(), "USER");
            return ResponseEntity.ok(
                    Map.of("token", token));
        }

        return ResponseEntity.ok(
                Map.of("message", "OTP verified"));
    }



    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        String token = jwtUtil.generateToken(
                request.getEmail(), "USER");

        return ResponseEntity.ok(
                Map.of("token", token));
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful"));
    }
}
