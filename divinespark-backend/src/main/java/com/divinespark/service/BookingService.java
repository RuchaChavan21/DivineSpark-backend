package com.divinespark.service;

import com.divinespark.dto.UserBookingResponse;
import com.divinespark.dto.UserSessionBookingResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface BookingService {

    List<UserBookingResponse> getUserBookings(Long userId);
    void cancelBooking(Long bookingId, Long userId);
    long getTotalBookings();
    public void downloadSessionUsers(Long sessionId, HttpServletResponse response);

    UserSessionBookingResponse getMyBookingForSession(Long userId, Long sessionId);

}
