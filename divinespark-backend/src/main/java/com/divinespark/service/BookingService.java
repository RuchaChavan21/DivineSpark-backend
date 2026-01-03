package com.divinespark.service;

import com.divinespark.dto.UserBookingResponse;

import java.util.List;

public interface BookingService {

    List<UserBookingResponse> getUserBookings(Long userId);
    void cancelBooking(Long bookingId, Long userId);
    long getTotalBookings();
}
