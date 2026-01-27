package com.divinespark.dto;

import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.entity.enums.PaymentType;

public class UserSessionBookingResponse {

    private Long bookingId;
    private BookingStatus bookingStatus;
    private PaymentType paymentType;
    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;

    public UserSessionBookingResponse(
            Long bookingId,
            BookingStatus bookingStatus,
            PaymentType paymentType,
            double totalAmount,
            double paidAmount,
            double remainingAmount
    ) {
        this.bookingId = bookingId;
        this.bookingStatus = bookingStatus;
        this.paymentType = paymentType;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
    }

    public Long getBookingId() { return bookingId; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public PaymentType getPaymentType() { return paymentType; }
    public double getTotalAmount() { return totalAmount; }
    public double getPaidAmount() { return paidAmount; }
    public double getRemainingAmount() { return remainingAmount; }
}
