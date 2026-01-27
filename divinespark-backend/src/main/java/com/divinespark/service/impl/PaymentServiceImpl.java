package com.divinespark.service.impl;

import com.divinespark.dto.AdminPaymentResponse;
import com.divinespark.entity.*;
import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.entity.enums.InstallmentStatus;
import com.divinespark.entity.enums.PaymentType;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.InstallmentRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepo;
    private final EmailService emailService;
    private final InstallmentRepository installmentRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepo,
            EmailService emailService,
            InstallmentRepository installmentRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepo = bookingRepo;
        this.emailService = emailService;
        this.installmentRepository = installmentRepository;
    }

    // ================= FAILURE HANDLING =================

    @Transactional
    public void handlePaymentFailure(String gatewayOrderId) {

        Payment payment =
                paymentRepository.findByGatewayOrderId(gatewayOrderId);

        if (payment == null) return;
        if ("FAILED".equals(payment.getStatus())) return;

        payment.setStatus("FAILED");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
    }

    // ================= SUCCESS HANDLING (FULL PAYMENT) =================

    @Transactional
    public void handlePaymentSuccess(String gatewayOrderId) {

        Payment payment =
                paymentRepository.findByGatewayOrderId(gatewayOrderId);

        if (payment == null) return;
        if ("SUCCESS".equals(payment.getStatus())) return;

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        Booking booking = bookingRepo
                .findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentType() == PaymentType.FULL) {
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepo.save(booking);
        }
    }

    // ================= ADMIN =================

    @Override
    public List<AdminPaymentResponse> getAllPaymentsForAdmin() {
        return paymentRepository.fetchAdminPayments();
    }

    // ================= WEBHOOK (FULL + INSTALLMENT) =================

    @Override
    @Transactional
    public boolean handlePaymentCaptured(String razorpayOrderId, int amount) {

        // 1️⃣ INSTALLMENT PAYMENT (CHECK FIRST)
        Installment installment =
                installmentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElse(null);

        if (installment != null) {

            if (installment.getStatus() == InstallmentStatus.PAID) {
                return true; // idempotent
            }

            installment.setStatus(InstallmentStatus.PAID);
            installment.setPaidAt(OffsetDateTime.now());
            installmentRepository.save(installment);

            Booking booking = installment.getBooking();

            double paidAmount = installmentRepository
                    .findByBooking_IdAndStatus(
                            booking.getId(),
                            InstallmentStatus.PAID
                    )
                    .stream()
                    .mapToDouble(Installment::getAmount)
                    .sum();

            booking.setPaidAmount(paidAmount);
            booking.setRemainingAmount(
                    Math.max(booking.getTotalAmount() - paidAmount, 0)
            );

            booking.setBookingStatus(
                    booking.getRemainingAmount() == 0
                            ? BookingStatus.CONFIRMED
                            : BookingStatus.PARTIALLY_PAID
            );

            bookingRepo.save(booking);

            // Payment record (for admin / audit)
            Payment p = new Payment();
            p.setBookingId(booking.getId());
            p.setAmount(installment.getAmount());
            p.setGatewayOrderId(razorpayOrderId);
            p.setStatus("SUCCESS");
            paymentRepository.save(p);

            return true;
        }

        // 2️⃣ FULL PAYMENT (CHECK AFTER)
        Payment payment =
                paymentRepository.findByGatewayOrderId(razorpayOrderId);

        if (payment != null) {

            if ("SUCCESS".equals(payment.getStatus())) {
                return true;
            }

            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            Booking booking = bookingRepo.findById(payment.getBookingId())
                    .orElseThrow();

            booking.setPaidAmount(booking.getTotalAmount());
            booking.setRemainingAmount(0);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepo.save(booking);

            return true;
        }

        return false;
    }


}
