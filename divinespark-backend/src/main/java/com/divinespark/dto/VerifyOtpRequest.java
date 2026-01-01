package com.divinespark.dto;

import com.divinespark.entity.enums.OtpPurpose;
import com.divinespark.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

public class VerifyOtpRequest {

    @NotNull
    private String email;

    @NotNull
    private String otp;

    @NotNull
    private OtpPurpose purpose;

    @NotNull
    private Role role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public OtpPurpose getPurpose() { return purpose; }
    public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}