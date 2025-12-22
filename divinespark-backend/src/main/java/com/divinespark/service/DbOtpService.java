package com.divinespark.service;



import com.divinespark.entity.OtpPurpose;
import com.divinespark.entity.OtpVerification;
import com.divinespark.repository.OtpRepository;
import com.divinespark.service.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class DbOtpService implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public DbOtpService(OtpRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }


    @Override
    public void generateAndSendOtp(String email, OtpPurpose purpose) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpVerification entity = OtpVerification.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .verified(false)
                .attempts(0)
                .build();

        otpRepository.save(entity);

        emailService.sendOtpEmail(email, otp, purpose);
    }

    @Override
    public void verifyOtp(String email, String otp, OtpPurpose purpose) {

        OtpVerification record = otpRepository
                .findTopByEmailAndOtpAndPurposeAndVerifiedFalse(
                        email, otp, purpose
                )
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if(record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Otp expired");
        }

        record.setVerified(true);
        otpRepository.save(record);
    }

}
