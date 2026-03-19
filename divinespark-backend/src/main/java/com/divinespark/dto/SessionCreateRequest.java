package com.divinespark.dto;

import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;

import java.time.OffsetDateTime;

public class SessionCreateRequest {

    private String title;
    private String description;
    private SessionType type;
    private double price;
    private String whatsappGroupLink;

    //  Frontend sends OffsetDateTime with +05:30
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private int maxSeats;
    private String guideName;
    private SessionStatus status;

    public SessionCreateRequest() {
        // no-args constructor
    }

    // ---------- Getters & Setters ----------

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

    public String getWhatsappGroupLink() {
        return whatsappGroupLink;
    }

    public void setWhatsappGroupLink(String whatsappGroupLink) {
        this.whatsappGroupLink = whatsappGroupLink;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
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

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
