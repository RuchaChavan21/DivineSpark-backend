package com.divinespark.dto;

import java.time.OffsetDateTime;

public class BlogListResponse {
    private String title;
    private String slug;
    private String excerpt;
    private String authorName;
    private String authorRole;
    private OffsetDateTime createdAt;
    private Long id;


    private Boolean isPublished;

    public Boolean getIsPublished() {
        return isPublished;
    }
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }


    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}