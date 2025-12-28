package com.divinespark.service.impl;

import com.divinespark.dto.UserBookingResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Session;
import com.divinespark.repository.BookingRepository;
import com.divinespark.service.BookingService;
import com.divinespark.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    private final EmailService emailService;
    public BookingServiceImpl(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
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

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure user owns the booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized cancellation");
        }

        // Prevent double cancellation
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }

        // Cancel booking
        booking.setStatus("CANCELLED");

        // Restore seat
        Session session = booking.getSession();
        session.setAvailableSeats(session.getAvailableSeats() + 1);

        bookingRepository.save(booking);

        // Send email AFTER successful DB update
        emailService.sendBookingCancelledEmail(
                booking.getUser().getEmail(),
                session.getTitle(),
                session.getStartTime().toString()
        );
    }


}
