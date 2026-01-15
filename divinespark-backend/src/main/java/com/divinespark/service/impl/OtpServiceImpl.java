package com.divinespark.service.impl;

import com.divinespark.entity.OtpVerification;
import com.divinespark.entity.enums.OtpPurpose;
import com.divinespark.repository.OtpRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.OtpService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Primary

public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpServiceImpl(OtpRepository otpRepository,
                          EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Override
    public void generateAndSendOtp(String email, OtpPurpose purpose) {

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000));

        OtpVerification entity = new OtpVerification();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setPurpose(purpose);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);

        otpRepository.save(entity);

        emailService.sendOtpEmail(email, otp, purpose);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp, OtpPurpose purpose) {

        OtpVerification entity =
                otpRepository.findTopByEmailAndOtpAndPurposeAndVerifiedFalse(
                        email, otp, purpose
                ).orElseThrow(() ->
                        new RuntimeException("Invalid or expired OTP"));

        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // 🔑 IMPORTANT LOGIC
        if (purpose == OtpPurpose.VERIFY_EMAIL) {
            entity.setVerified(true); // consume OTP
        }
        // FORGOT_PASSWORD → only validate, DO NOT consume
    }
}
