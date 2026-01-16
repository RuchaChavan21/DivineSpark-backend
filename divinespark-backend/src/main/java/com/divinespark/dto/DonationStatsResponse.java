package com.divinespark.dto;

import java.util.List;

public class DonationStatsResponse {

    private double totalAmount;
    private long totalDonors;
    private List<MonthlyDonationResponse> monthlyDonations;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public List<MonthlyDonationResponse> getMonthlyDonations() {
        return monthlyDonations;
    }

    public void setMonthlyDonations(List<MonthlyDonationResponse> monthlyDonations) {
        this.monthlyDonations = monthlyDonations;
    }
}
