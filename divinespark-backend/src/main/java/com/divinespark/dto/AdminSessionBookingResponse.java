package com.divinespark.dto;

import java.time.LocalDateTime;

public class AdminSessionBookingResponse {

    private Long bookingId;
    private Long userId;
    private String email;
    private String username;
    private String bookingStatus;
    private LocalDateTime bookedAt;

    public AdminSessionBookingResponse(
            Long bookingId,
            Long userId,
            String email,
            String bookingStatus,
            LocalDateTime bookedAt) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.email = email;
        this.bookingStatus = bookingStatus;
        this.bookedAt = bookedAt;
    }

    public Long getBookingId() { return bookingId; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getBookingStatus() { return bookingStatus; }
    public LocalDateTime getBookedAt() { return bookedAt; }
}
