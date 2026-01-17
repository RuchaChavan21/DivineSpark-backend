package com.divinespark.dto;

import java.time.OffsetDateTime;

public class ReviewResponse {

    private int rating;
    private String comment;
    private String userName;
    private OffsetDateTime createdAt;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {return userName;}

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
