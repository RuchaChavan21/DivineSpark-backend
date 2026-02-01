package com.divinespark.service.impl;

import com.divinespark.dto.AdminSessionUserResponse;
import com.divinespark.dto.UserBookingResponse;
import com.divinespark.dto.UserSessionBookingResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Session;
import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.repository.BookingRepository;
import com.divinespark.service.BookingService;
import com.divinespark.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EmailService emailService) {
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
            dto.setStatus(booking.getBookingStatus());
            dto.setStartTime(session.getStartTime().toLocalDateTime());
            dto.setEndTime(session.getEndTime().toLocalDateTime());

            if (
                    (booking.getBookingStatus() == BookingStatus.CONFIRMED ||
                            booking.getBookingStatus() == BookingStatus.PARTIALLY_PAID) &&
                            session.getStatus() == SessionStatus.UPCOMING
            )
 {
                dto.setJoinLink(session.getWhatsLink());
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

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized cancellation");
        }

        if ("CANCELLED".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        Session session = booking.getSession();
        session.getAvailableSeats().incrementAndGet();

        bookingRepository.save(booking);

        emailService.sendBookingCancelledEmail(
                booking.getUser().getEmail(),
                session.getTitle(),
                session.getStartTime().toString()
        );
    }

    @Override
    public long getTotalBookings() {
        return bookingRepository.count();
    }

    @Override
    public void downloadSessionUsers(Long sessionId, HttpServletResponse response) {

        List<AdminSessionUserResponse> users =
                bookingRepository.findUsersBySessionId(sessionId);

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=session-" + sessionId + "-users.csv"
        );

        try (PrintWriter writer = response.getWriter()) {

            writer.println("Username,Email,Contact Number,Booking Status");

            for (AdminSessionUserResponse u : users) {
                writer.println(
                        u.getUsername() + "," +
                                u.getEmail() + "," +
                                u.getContactNumber() + "," +
                                u.getBookingStatus()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to download users");
        }
    }

    @Override
    public UserSessionBookingResponse getMyBookingForSession(
            Long userId,
            Long sessionId
    ) {

        Booking booking = bookingRepository
                .findByUserIdAndSessionId(userId, sessionId)
                .orElseThrow(() -> new RuntimeException("No booking found"));

        return new UserSessionBookingResponse(
                booking.getId(),
                booking.getBookingStatus(),
                booking.getPaymentType(),
                booking.getTotalAmount(),
                booking.getPaidAmount(),
                booking.getRemainingAmount()
        );
    }


}
