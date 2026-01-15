package com.divinespark.dto;

import java.util.List;

public class UserBookingListResponse {

    private List<UserBookingResponse> upcoming;
    private List<UserBookingResponse> past;

    public List<UserBookingResponse> getUpcoming() { return upcoming; }
    public void setUpcoming(List<UserBookingResponse> upcoming) {
        this.upcoming = upcoming;
    }

    public List<UserBookingResponse> getPast() { return past; }
    public void setPast(List<UserBookingResponse> past) {
        this.past = past;
    }
}
