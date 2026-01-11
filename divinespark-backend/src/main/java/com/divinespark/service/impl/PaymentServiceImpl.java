package com.divinespark.service.impl;

import com.divinespark.dto.AdminPaymentResponse;
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

import java.util.ArrayList;
import java.util.List;


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

        Payment payment = paymentRepository
                .findByGatewayOrderId(req.getGatewayOrderId());

        if (payment == null) {
            throw new RuntimeException("Invalid payment reference");
        }

        // Idempotency
        if ("SUCCESS".equals(payment.getStatus()) ||
                "FAILED".equals(payment.getStatus())) {
            return;
        }

        Booking booking = bookingRepo.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"PENDING".equals(booking.getStatus())) {
            return;
        }

        if (!"SUCCESS".equals(req.getPaymentStatus())) {
            payment.setStatus("FAILED");
            booking.setStatus("FAILED");
            return;
        }

        Session session = booking.getSession();

        if (session.getAvailableSeats().get() <= 0) {
            throw new RuntimeException("No seats available");
        }

        // Mark success
        payment.setStatus("SUCCESS");
        booking.setStatus("CONFIRMED");

        session.setAvailableSeats(
                session.getAvailableSeats().get() - 1
        );

        User user = booking.getUser();

        ZoomRegistrationResponse zoomResponse =
                zoomService.registerUser(
                        session.getZoomMeetingId(),
                        user.getEmail(),
                        ZoomNameUtil.getFirstName(user.getUsername()),
                        ZoomNameUtil.getLastName()
                );

        booking.setZoomRegistrantId(zoomResponse.getRegistrantId());

        if (zoomResponse.getJoinUrl() != null) {
            booking.setZoomJoinUrl(zoomResponse.getJoinUrl());
        }


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

    @Override
    public List<AdminPaymentResponse> getAllPaymentsForAdmin() {
        return paymentRepository.fetchAdminPayments();
    }

    @Override
    @Transactional
    public void handlePaymentCaptured(String razorpayOrderId, int amount) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(razorpayOrderId);

        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        // Idempotency
        if ("SUCCESS".equals(payment.getStatus())) {
            return;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CONFIRMED");
        bookingRepo.save(booking);
    }

}