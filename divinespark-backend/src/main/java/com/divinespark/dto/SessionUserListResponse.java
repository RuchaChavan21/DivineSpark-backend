package com.divinespark.dto;

import java.util.List;

public class SessionUserListResponse {

    private List<SessionUserResponse> sessions;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public List<SessionUserResponse> getSessions() {
        return sessions;
    }
    public void setSessions(List<SessionUserResponse> sessions) {
        this.sessions = sessions;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
