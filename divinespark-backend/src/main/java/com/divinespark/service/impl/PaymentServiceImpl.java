package com.divinespark.service.impl;

import com.divinespark.dto.AdminPaymentResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Payment;
import com.divinespark.entity.Session;
import com.divinespark.entity.User;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepo;
    private final EmailService emailService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepo,
            EmailService emailService) {

        this.paymentRepository = paymentRepository;
        this.bookingRepo = bookingRepo;
        this.emailService = emailService;
    }

    @Transactional
    public void handlePaymentFailure(String gatewayOrderId) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(gatewayOrderId);

        if ("FAILED".equals(payment.getStatus())) {
            return;
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
    public boolean handlePaymentCaptured(String razorpayOrderId, int amount) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(razorpayOrderId);

        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }

        if ("SUCCESS".equals(payment.getStatus())) {
            return false;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CONFIRMED");

        // Zoom registration for access control (no join_url dependency)
        Session session = booking.getSession();
        User user = booking.getUser();

        bookingRepo.save(booking);
        return false;
    }
}
