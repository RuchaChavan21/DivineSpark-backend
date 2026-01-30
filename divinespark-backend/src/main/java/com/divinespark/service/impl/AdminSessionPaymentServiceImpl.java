package com.divinespark.service.impl;



import com.divinespark.dto.*;
import com.divinespark.entity.Booking;
import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.SessionRepository;
import com.divinespark.service.AdminSessionPaymentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminSessionPaymentServiceImpl
        implements AdminSessionPaymentService {

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;

    public AdminSessionPaymentServiceImpl(
            BookingRepository bookingRepository,
            SessionRepository sessionRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public AdminSessionOverviewResponse getSessionOverview(Long sessionId) {

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<Booking> bookings =
                bookingRepository.findDetailedBookingsBySessionId(sessionId);

        AdminSessionOverviewResponse res =
                new AdminSessionOverviewResponse();

        res.setSessionId(sessionId);
        res.setSessionTitle(session.getTitle());

        int fullyPaid = 0, partial = 0, pending = 0;
        double collected = 0, expected = 0;

        List<AdminSessionUserPaymentResponse> users =
                new ArrayList<>();

        for (Booking b : bookings) {

            expected += b.getTotalAmount();
            collected += b.getPaidAmount();

            if (b.getBookingStatus() == BookingStatus.CONFIRMED)
                fullyPaid++;
            else if (b.getBookingStatus() == BookingStatus.PARTIALLY_PAID)
                partial++;
            else
                pending++;

            AdminSessionUserPaymentResponse u =
                    new AdminSessionUserPaymentResponse();

            u.setBookingId(b.getId());
            u.setUserEmail(b.getUser().getEmail());
            u.setUsername(b.getUser().getUsername());
            u.setContactNumber(b.getUser().getContactNumber());
            u.setPaymentType(b.getPaymentType().name());
            u.setBookingStatus(b.getBookingStatus().name());
            u.setTotalAmount(b.getTotalAmount());
            u.setPaidAmount(b.getPaidAmount());
            u.setRemainingAmount(b.getRemainingAmount());

            users.add(u);
        }

        res.setTotalBookings(bookings.size());
        res.setFullyPaid(fullyPaid);
        res.setPartiallyPaid(partial);
        res.setPending(pending);
        res.setTotalCollected(collected);
        res.setExpectedRevenue(expected);
        res.setUsers(users);

        return res;
    }
}
