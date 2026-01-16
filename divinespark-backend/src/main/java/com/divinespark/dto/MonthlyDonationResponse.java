package com.divinespark.dto;

public class MonthlyDonationResponse {

    private int month;
    private int year;
    private double amount;

    public MonthlyDonationResponse(int month, int year, double amount) {
        this.month = month;
        this.year = year;
        this.amount = amount;
    }

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getAmount() { return amount; }
}
