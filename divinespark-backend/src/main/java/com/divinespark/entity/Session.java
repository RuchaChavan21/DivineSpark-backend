package com.divinespark.entity;

import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.*;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "sessions")
public class Session {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SessionType type;

    @Column(nullable = false)
    private double price;

    @Column(name = "whatsapp-link")
    private String whatsLink;

    @Column(columnDefinition = "DATETIME")
    private OffsetDateTime startTime;

    @Column(columnDefinition = "DATETIME")
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.UPCOMING;

    @Convert(converter = AtomicIntegerConverter.class)
    @Column(nullable = false)
    private AtomicInteger maxSeats = new AtomicInteger(0);

    @Convert(converter = AtomicIntegerConverter.class)
    @Column(nullable = false)
    private AtomicInteger availableSeats = new AtomicInteger(0);

    @Column(nullable = false)
    private String guideName;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    private String recordingUrl;

    @Lob
    @JsonIgnore
    @Column(name = "thumbnail_data", columnDefinition = "MEDIUMBLOB")
    private byte[] thumbnailData;

    @Column(name = "has_thumbnail", nullable = false)
    private boolean hasThumbnail = false;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now(IST);
        availableSeats = maxSeats;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(IST);
    }

    // getters/setters unchanged


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

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getWhatsLink() {
        return whatsLink;
    }

    public void setWhatsLink(String whatsLink) {
        this.whatsLink = whatsLink;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public AtomicInteger getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(AtomicInteger maxSeats) {
        this.maxSeats = maxSeats;
    }

    public AtomicInteger getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(AtomicInteger availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }


    public byte[] getThumbnailData() { return thumbnailData; }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
        this.hasThumbnail = (thumbnailData != null && thumbnailData.length > 0);
    }

    public boolean isHasThumbnail() { return hasThumbnail; }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }
}
