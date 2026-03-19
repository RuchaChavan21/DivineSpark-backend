package com.divinespark.service;

import com.divinespark.dto.LoginRequest;
import com.divinespark.dto.RegisterRequest;
import com.divinespark.entity.OtpVerification;
import com.divinespark.entity.User;
import com.divinespark.entity.enums.OtpPurpose;
import com.divinespark.entity.enums.Role;
import com.divinespark.repository.OtpRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        String phone = request.getContactNumber().replaceAll("\\D", "");

        if (phone.startsWith("91") && phone.length() == 12) {
            phone = phone.substring(2);
        }

        if (!phone.matches("^[6-9]\\d{9}$")) {
            throw new RuntimeException("Invalid contact number");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .contactNumber(phone)
                .role(Role.USER)
                .isActive(true)
                .build();

        userRepository.save(user);
    }
    // ================= LOGIN =================
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    // ================= RESET PASSWORD =================
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {

        OtpVerification otpEntity =
                otpRepository.findTopByEmailAndOtpAndPurposeAndVerifiedFalse(
                        email,
                        otp,
                        OtpPurpose.FORGOT_PASSWORD
                ).orElseThrow(() ->
                        new RuntimeException("Invalid or expired OTP"));

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpEntity.setVerified(true); // ✅ OTP CONSUMED HERE
    }
}
