package com.divinespark.service.impl;

import com.divinespark.dto.UserBookingResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Session;
import com.divinespark.repository.BookingRepository;
import com.divinespark.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<UserBookingResponse> getUserBookings(Long userId) {


        List<Booking> bookings =
                bookingRepository.findUserBookingsWithSession(userId);

        List<UserBookingResponse> response = new ArrayList<>();

        for (Booking booking : bookings) {

            Session session = booking.getSession();

            UserBookingResponse dto = new UserBookingResponse();
            dto.setBookingId(booking.getId());
            dto.setSessionId(session.getId());
            dto.setSessionTitle(session.getTitle());
            dto.setSessionType(session.getType().name());
            dto.setBookingStatus(booking.getStatus());
            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());

            // Show join link ONLY if allowed
            if (
                    booking.getStatus().equals("CONFIRMED") &&
                            session.getStatus().name().equals("UPCOMING")
            ) {
                if (session.getType().name().equals("FREE")) {
                    dto.setJoinLink(session.getZoomLink());
                }
            }

            response.add(dto);
        }

        return response;
    }

}
