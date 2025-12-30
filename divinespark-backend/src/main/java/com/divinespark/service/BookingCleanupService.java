package com.divinespark.service;


import com.divinespark.entity.Booking;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public BookingCleanupService(BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 5 * 60 * 1000) //5 min
    public void cleanupStalePendingBookings() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        List<Booking> staleBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                "PENDING" ,
                cutoff
        );

        for(Booking booking : staleBookings) {

            booking.setStatus("PENDING");

            paymentRepository
                    .findTopByBookingIdOrderByCreatedAtDesc(booking.getId())
                    .ifPresent(payment -> payment.setStatus("FAILED"));
        }
    }
}
