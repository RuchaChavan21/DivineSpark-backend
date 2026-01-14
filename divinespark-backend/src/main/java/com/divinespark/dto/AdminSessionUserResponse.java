package com.divinespark.dto;

public class AdminSessionUserResponse {

    private Long userId;
    private String email;
    private String username;
    private String contactNumber;
    private String bookingStatus;

    public AdminSessionUserResponse(Long userId, String email, String bookingStatus, String username, String contactNumber) {
        this.userId = userId;
        this.email = email;
        this.bookingStatus = bookingStatus;
        this.username = username;
        this.contactNumber = contactNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getUsername() {return username;}

    public String getContactNumber() {return contactNumber;}
}
