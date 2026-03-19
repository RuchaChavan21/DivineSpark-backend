package com.divinespark.dto;

import java.time.OffsetDateTime;

public class BlogDetailResponse {

    private String title;
    private String content;
    private String authorName;
    private String authorRole;
    private OffsetDateTime createdAt;


    private Boolean isPublished;

    public Boolean getIsPublished() {
        return isPublished;
    }
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}