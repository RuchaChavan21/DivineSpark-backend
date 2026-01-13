package com.divinespark.dto;

public class DonationCreateRequest {
    private double amount;
    private String note;

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
