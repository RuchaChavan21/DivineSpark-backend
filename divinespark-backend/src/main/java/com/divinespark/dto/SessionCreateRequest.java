package com.divinespark.dto;

import com.divinespark.entity.enums.SessionType;
import java.time.LocalDateTime;

public class SessionCreateRequest {

    private String title;
    private String description;
    private SessionType type;
    private double price;
    private String freeZoomLink;
    private String paidZoomLink;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxSeats;
    private String guideName;

    public SessionCreateRequest() {
        // no-args constructor
    }

    // ---------- Getters and Setters ---------- //

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFreeZoomLink() {
        return freeZoomLink;
    }

    public void setFreeZoomLink(String freeZoomLink) {
        this.freeZoomLink = freeZoomLink;
    }

    public String getPaidZoomLink() {
        return paidZoomLink;
    }

    public void setPaidZoomLink(String paidZoomLink) {
        this.paidZoomLink = paidZoomLink;
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

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }
}
