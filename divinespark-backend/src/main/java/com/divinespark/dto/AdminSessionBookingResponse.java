package com.divinespark.dto;

import com.divinespark.entity.enums.BookingStatus;

import java.time.OffsetDateTime;

public class AdminSessionBookingResponse {

    private Long bookingId;
    private Long userId;
    private String email;
    private String username;
    private String phoneNumber;
    private BookingStatus bookingStatus;
    private OffsetDateTime bookedAt;

    public AdminSessionBookingResponse(
            Long bookingId,
            Long userId,
            String username,
            String phoneNumber,
            String email,
            BookingStatus bookingStatus,
            OffsetDateTime bookedAt) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bookingStatus = bookingStatus;
        this.bookedAt = bookedAt;
    }




    public Long getBookingId() { return bookingId; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public OffsetDateTime getBookedAt() { return bookedAt; }
    public String getPhoneNumber() { return phoneNumber; }

}
