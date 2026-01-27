package com.divinespark.dto;

public class DonationCreateRequest {
    private int amount;
    private String note;

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
