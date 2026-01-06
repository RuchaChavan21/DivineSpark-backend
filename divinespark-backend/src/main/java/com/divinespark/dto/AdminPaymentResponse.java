package com.divinespark.dto;

import java.time.LocalDateTime;

public class AdminPaymentResponse {

    private Long paymentId;
    private String userEmail;
    private String sessionTitle;
    private double amount;
    private String status;
    private String gatewayOrderId;
    private LocalDateTime createdAt;

    public AdminPaymentResponse(
            Long paymentId,
            String userEmail,
            String sessionTitle,
            double amount,
            String status,
            String gatewayOrderId,
            LocalDateTime createdAt
    ) {
        this.paymentId = paymentId;
        this.userEmail = userEmail;
        this.sessionTitle = sessionTitle;
        this.amount = amount;
        this.status = status;
        this.gatewayOrderId = gatewayOrderId;
        this.createdAt = createdAt;
    }

    public Long getPaymentId() { return paymentId; }
    public String getUserEmail() { return userEmail; }
    public String getSessionTitle() { return sessionTitle; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getGatewayOrderId() { return gatewayOrderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
