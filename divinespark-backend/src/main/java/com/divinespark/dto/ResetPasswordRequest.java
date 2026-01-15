package com.divinespark.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String otp;

    @NotBlank
    private String newPassword;

    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public String getNewPassword() { return newPassword; }

    public void setEmail(String email) { this.email = email; }
    public void setOtp(String otp) { this.otp = otp; }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
