package com.divinespark.dto;

import com.divinespark.entity.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class RequestOtpRequest {

    @Email
    @NotNull
    private String email;

    @NotNull
    private OtpPurpose purpose;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public OtpPurpose getPurpose() { return purpose; }
    public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }
}
