package com.divinespark.dto;

import com.divinespark.entity.enums.InstallmentStatus;
import java.time.OffsetDateTime;

public class AdminInstallmentResponse {

    private Long installmentId;
    private int installmentNumber;
    private double amount;
    private InstallmentStatus status;
    private OffsetDateTime paidAt;

    public AdminInstallmentResponse(
            Long installmentId,
            int installmentNumber,
            double amount,
            InstallmentStatus status,
            OffsetDateTime paidAt
    ) {
        this.installmentId = installmentId;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public Long getInstallmentId() { return installmentId; }
    public int getInstallmentNumber() { return installmentNumber; }
    public double getAmount() { return amount; }
    public InstallmentStatus getStatus() { return status; }
    public OffsetDateTime getPaidAt() { return paidAt; }
}
