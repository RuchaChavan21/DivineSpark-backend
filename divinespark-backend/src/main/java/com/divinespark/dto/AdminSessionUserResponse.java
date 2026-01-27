package com.divinespark.dto;

import com.divinespark.entity.enums.BookingStatus;

public class AdminSessionUserResponse {

    private Long userId;
    private String email;
    private String username;
    private String contactNumber;
    private BookingStatus bookingStatus;

    public AdminSessionUserResponse(Long userId, String email, BookingStatus bookingStatus, String username, String contactNumber) {
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

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public String getUsername() {return username;}

    public String getContactNumber() {return contactNumber;}
}
