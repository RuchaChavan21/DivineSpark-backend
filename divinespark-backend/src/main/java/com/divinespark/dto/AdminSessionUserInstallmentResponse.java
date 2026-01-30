package com.divinespark.dto;


import com.divinespark.entity.enums.BookingStatus;
import java.util.List;

public class AdminSessionUserInstallmentResponse {

    private Long bookingId;
    private Long userId;
    private String username;
    private String email;
    private String contactNumber;

    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;

    private BookingStatus bookingStatus;
    private List<AdminInstallmentResponse> installments;

    public AdminSessionUserInstallmentResponse(
            Long bookingId,
            Long userId,
            String username,
            String email,
            String contactNumber,
            double totalAmount,
            double paidAmount,
            double remainingAmount,
            BookingStatus bookingStatus,
            List<AdminInstallmentResponse> installments
    ) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.contactNumber = contactNumber;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
        this.bookingStatus = bookingStatus;
        this.installments = installments;
    }

    public Long getBookingId() { return bookingId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public double getTotalAmount() { return totalAmount; }
    public double getPaidAmount() { return paidAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public List<AdminInstallmentResponse> getInstallments() { return installments; }
}
