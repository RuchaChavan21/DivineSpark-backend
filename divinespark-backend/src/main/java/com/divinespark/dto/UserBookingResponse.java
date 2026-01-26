package com.divinespark.dto;

import com.divinespark.entity.enums.BookingStatus;

import java.time.LocalDateTime;

public class UserBookingResponse {

    private Long bookingId;
    private Long sessionId;
    private String sessionTitle;
    private String sessionType;
    private BookingStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String whatsLink; // nullable

    public Long getBookingId() {
        return bookingId;
    }
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getSessionId() {
        return sessionId;
    }
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }
    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getSessionType() {
        return sessionType;
    }
    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getJoinLink() {
        return whatsLink;
    }
    public void setJoinLink(String whatsLink) {
        this.whatsLink = whatsLink;
    }
}
