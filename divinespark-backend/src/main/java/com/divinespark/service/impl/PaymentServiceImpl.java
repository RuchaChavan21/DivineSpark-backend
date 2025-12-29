package com.divinespark.service.impl;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.PaymentInitiateResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Payment;
import com.divinespark.entity.Session;
import com.divinespark.entity.User;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.repository.SessionRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.PaymentService;
import com.divinespark.service.RazorpayService;
import com.divinespark.dto.RazorpayOrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;

    public PaymentServiceImpl(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            RazorpayService razorpayService
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    /**
     * STEP-1: Initiate payment
     * - Creates booking (PENDING)
     * - Creates Razorpay order
     * - Saves payment (CREATED)
     */
    @Transactional
    @Override
    public PaymentInitiateResponse initiatePayment(Long sessionId, String userEmail) {

        // 1️⃣ Fetch user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Fetch session
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        // 3️⃣ Create booking (PENDING)
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSession(session);
        booking.setStatus("PENDING");
        bookingRepository.save(booking);

        // 4️⃣ Create Razorpay order (STUB / REAL later)
        RazorpayOrderResponse razorpayOrder =
                razorpayService.createOrder(
                        session.getPrice(),
                        booking.getId()
                );

        // 5️⃣ Save payment (CREATED)
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setGatewayOrderId(razorpayOrder.getOrderId());
        payment.setAmount(session.getPrice());
        payment.setStatus("CREATED");
        paymentRepository.save(payment);

        // 6️⃣ Return response to frontend
        PaymentInitiateResponse response = new PaymentInitiateResponse();
        response.setBookingId(booking.getId());
        response.setOrderId(razorpayOrder.getOrderId());
        response.setAmount(session.getPrice());
        response.setCurrency("INR");

        return response;
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

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Session session = booking.getSession();

        if (session.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats left");
        }

        // Confirm booking
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        // Reduce seats
        session.setAvailableSeats(session.getAvailableSeats() - 1);

        User user = booking.getUser();

        // Zoom + email will be executed later (already implemented by you)
    }



}
