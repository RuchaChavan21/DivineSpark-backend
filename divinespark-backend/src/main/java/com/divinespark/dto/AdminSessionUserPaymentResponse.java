package com.divinespark.dto;

public class AdminSessionUserPaymentResponse {

    private Long bookingId;
    private String userEmail;
    private String username;
    private String contactNumber;

    private String paymentType;     // FULL / INSTALLMENT
    private String bookingStatus;   // CONFIRMED / PARTIALLY_PAID / PENDING

    private double totalAmount;
    private double paidAmount;
    private double remainingAmount;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public AdminSessionUserPaymentResponse(Long bookingId, String userEmail, String username, String contactNumber, String paymentType, String bookingStatus, double paidAmount, double totalAmount, double remainingAmount) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.username = username;
        this.contactNumber = contactNumber;
        this.paymentType = paymentType;
        this.bookingStatus = bookingStatus;
        this.paidAmount = paidAmount;
        this.totalAmount = totalAmount;
        this.remainingAmount = remainingAmount;
    }

    public AdminSessionUserPaymentResponse() {}



}
