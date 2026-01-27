package com.divinespark.dto;

import com.divinespark.entity.enums.InstallmentStatus;

import java.time.OffsetDateTime;

public class InstallmentResponse {

    private Long id;
    private int installmentNumber;
    private double amount;
    private InstallmentStatus status;
    private OffsetDateTime paidAt;

    public InstallmentResponse(
            Long id,
            int installmentNumber,
            double amount,
            InstallmentStatus status,
            OffsetDateTime paidAt
    ) {
        this.id = id;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public int getInstallmentNumber() { return installmentNumber; }
    public double getAmount() { return amount; }
    public InstallmentStatus getStatus() { return status; }
    public OffsetDateTime getPaidAt() { return paidAt; }
}
