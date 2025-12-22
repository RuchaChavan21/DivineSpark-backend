package com.divinespark.service;

import com.divinespark.entity.enums.OtpPurpose;

public interface OtpService {

    void generateAndSendOtp(String email, OtpPurpose purpose);
    void verifyOtp(String email, String otp, OtpPurpose purpose);
}