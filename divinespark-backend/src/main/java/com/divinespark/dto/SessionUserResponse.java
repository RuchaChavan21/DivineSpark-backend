package com.divinespark.dto;

import com.divinespark.entity.enums.SessionType;

import java.time.LocalDateTime;

public class SessionUserResponse {
    private Long id;
    private String title;
    private String description;
    private SessionType type;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String guideName;
    private int availableSeats;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getGuideName() {
        return guideName;
    }
    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
