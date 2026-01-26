package com.divinespark.dto;

import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;

import java.time.OffsetDateTime;

public class SessionListItemResponse {

    private Long id;
    private String title;
    private String description;
    private SessionType type;
    private double price;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private SessionStatus status;
    private int maxSeats;
    private int availableSeats;
    private String guideName;
    private boolean hasThumbnail;


    // ---------- getters & setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SessionType getType() { return type; }
    public void setType(SessionType type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public int getMaxSeats() { return maxSeats; }
    public void setMaxSeats(int maxSeats) { this.maxSeats = maxSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public boolean isHasThumbnail() { return hasThumbnail; }
    public void setHasThumbnail(boolean hasThumbnail) { this.hasThumbnail = hasThumbnail; }
}
