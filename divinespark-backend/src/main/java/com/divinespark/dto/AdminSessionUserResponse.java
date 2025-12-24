package com.divinespark.dto;

public class AdminSessionUserResponse {

    private Long userId;
    private String email;

    private String bookingStatus;

    public AdminSessionUserResponse(Long userId, String email, String bookingStatus) {
        this.userId = userId;
        this.email = email;
        this.bookingStatus = bookingStatus;
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
}
