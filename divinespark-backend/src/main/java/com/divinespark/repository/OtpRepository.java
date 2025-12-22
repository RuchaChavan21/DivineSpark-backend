package com.divinespark.repository;


import com.divinespark.entity.OtpPurpose;
import com.divinespark.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndOtpAndPurposeAndVerifiedFalse(
            String email,
            String otp,
            OtpPurpose purpose
    );


    void deleteByExpiresAtBefore(LocalDateTime now);
}