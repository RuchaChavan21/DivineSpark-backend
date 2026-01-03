package com.divinespark.dto;

public class AdminStatsResponse {

    private long totalUsers;
    private long totalBookings;
    private long upcomingSessions;
    private long pastSessions;
    private long totalSessions;

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }


    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(long upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public long getPastSessions() {
        return pastSessions;
    }

    public void setPastSessions(long pastSessions) {
        this.pastSessions = pastSessions;
    }
}
