package com.divinespark.service.impl;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.ZoomRegistrationResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Payment;
import com.divinespark.entity.Session;
import com.divinespark.entity.User;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.PaymentService;
import com.divinespark.service.ZoomService;
import com.divinespark.utils.ZoomNameUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepo;
    private final EmailService emailService;
    private final ZoomService zoomService;


    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, EmailService emailService, ZoomService zoomService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepo = bookingRepository;
        this.emailService = emailService;
        this.zoomService = zoomService;
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

        if (session.getAvailableSeats().get() <= 0) {
            throw new RuntimeException("No seats left");
        }

        // Confirm booking
        booking.setStatus("CONFIRMED");
        bookingRepo.save(booking);

        // Reduce seats
        session.setAvailableSeats(
                session.getAvailableSeats().decrementAndGet()
        );

        User user = booking.getUser();

        String firstName = ZoomNameUtil.getFirstName(user.getUsername());
        String lastName = ZoomNameUtil.getLastName();

// Register user to Zoom meeting
        ZoomRegistrationResponse zoomResponse =
                zoomService.registerUser(
                        session.getZoomMeetingId(),
                        user.getEmail(),
                        firstName,
                        lastName
                );

// Save Zoom details in booking
        booking.setZoomRegistrantId(zoomResponse.getRegistrantId());
        booking.setZoomJoinUrl(zoomResponse.getJoinUrl());
        bookingRepo.save(booking);


        // Send PAID session link
        emailService.sendSessionJoinLink(
                user.getEmail(),
                session.getTitle(),
                booking.getZoomJoinUrl(),
                session.getGuideName(),
                session.getStartTime().toString(),
                session.getEndTime().toString(),
                "PAID"
        );

    }

    @Transactional
    public void handlePaymentFailure(String gatewayOrderId) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(gatewayOrderId);

        if ("FAILED".equals(payment.getStatus())) {
            return; // idempotent
        }

        payment.setStatus("FAILED");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("FAILED");
        bookingRepo.save(booking);
    }

    @Transactional
    public void handlePaymentSuccess(String gatewayOrderId) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(gatewayOrderId);

        if ("SUCCESS".equals(payment.getStatus())) {
            return;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow();

        booking.setStatus("CONFIRMED");
        bookingRepo.save(booking);
    }


}