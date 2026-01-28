package com.divinespark.dto;

import java.util.List;

public class AdminSessionOverviewResponse {

    private Long sessionId;
    private String sessionTitle;

    private int totalBookings;
    private int fullyPaid;
    private int partiallyPaid;
    private int pending;

    private double totalCollected;
    private double expectedRevenue;

    private List<AdminSessionUserPaymentResponse> users;

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

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getFullyPaid() {
        return fullyPaid;
    }

    public void setFullyPaid(int fullyPaid) {
        this.fullyPaid = fullyPaid;
    }

    public int getPartiallyPaid() {
        return partiallyPaid;
    }

    public void setPartiallyPaid(int partiallyPaid) {
        this.partiallyPaid = partiallyPaid;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public double getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(double totalCollected) {
        this.totalCollected = totalCollected;
    }

    public double getExpectedRevenue() {
        return expectedRevenue;
    }

    public void setExpectedRevenue(double expectedRevenue) {
        this.expectedRevenue = expectedRevenue;
    }

    public List<AdminSessionUserPaymentResponse> getUsers() {
        return users;
    }

    public void setUsers(List<AdminSessionUserPaymentResponse> users) {
        this.users = users;
    }

    public AdminSessionOverviewResponse(Long sessionId, String sessionTitle, int totalBookings, int fullyPaid, int partiallyPaid, int pending, double totalCollected, double expectedRevenue, List<AdminSessionUserPaymentResponse> users) {
        this.sessionId = sessionId;
        this.sessionTitle = sessionTitle;
        this.totalBookings = totalBookings;
        this.fullyPaid = fullyPaid;
        this.partiallyPaid = partiallyPaid;
        this.pending = pending;
        this.totalCollected = totalCollected;
        this.expectedRevenue = expectedRevenue;
        this.users = users;
    }

    public AdminSessionOverviewResponse() {}
}
