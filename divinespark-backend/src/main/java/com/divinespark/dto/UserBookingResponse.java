package com.divinespark.dto;

import java.time.LocalDateTime;

public class UserBookingResponse {

    private Long bookingId;
    private Long sessionId;
    private String sessionTitle;
    private String sessionType;
    private String bookingStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String joinLink; // nullable

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

    public String getBookingStatus() {
        return bookingStatus;
    }
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
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
        return joinLink;
    }
    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }
}
