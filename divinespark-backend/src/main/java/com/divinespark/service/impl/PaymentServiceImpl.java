package com.divinespark.service.impl;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Payment;
import com.divinespark.entity.Session;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepo;
    private final EmailService emailService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepo = bookingRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public void handlePaymentCallback(PaymentCallbackRequest req) {

        Payment payment =
                paymentRepository.findByGatewayOrderId(req.getGatewayOrderId());

        if (payment == null) {
            throw new RuntimeException("Invalid payment reference");
        }

        // Idempotency check
        if ("SUCCESS".equals(payment.getStatus())) {
            return; // already processed
        }

        if (!"SUCCESS".equals(req.getPaymentStatus())) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return;
        }

        // Mark payment success
        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Booking booking = bookingRepo.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }

        Session session = booking.getSession();

        if (session.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats left");
        }

        // Confirm booking
        booking.setStatus("CONFIRMED");
        bookingRepo.save(booking);

        // Reduce seats
        session.setAvailableSeats(
                session.getAvailableSeats() - 1
        );

        // Send PAID session link
        emailService.sendFreeSessionLink(
                booking.getUser().getEmail(),
                session.getTitle(),
                session.getPaidZoomLink(),
                session.getGuideName(),
                session.getStartTime().toString(),
                session.getEndTime().toString()
        );
    }
}
