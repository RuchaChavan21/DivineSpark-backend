package com.divinespark.service.impl;


import com.divinespark.dto.InstallmentPayResponse;
import com.divinespark.dto.InstallmentResponse;
import com.divinespark.dto.RazorpayOrderResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Installment;
import com.divinespark.entity.Payment;
import com.divinespark.entity.enums.InstallmentStatus;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.InstallmentRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.service.InstallementService;
import com.divinespark.service.RazorpayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class InstallmentServiceImpl implements InstallementService {

    private final InstallmentRepository installmentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;

    public InstallmentServiceImpl(
            InstallmentRepository installmentRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            RazorpayService razorpayService
    ) {
        this.installmentRepository = installmentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    @Override
    @Transactional
    public InstallmentPayResponse payInstallment(Long installmentId, Long userId) {

        Installment installment = installmentRepository.findById(installmentId)
                .orElseThrow(() -> new RuntimeException("Installment not found"));

        Booking booking = installment.getBooking();

        // Security check
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        //  Already paid
        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new RuntimeException("Installment already paid");
        }

        // Enforce order: previous installment must be PAID
        if (installment.getInstallmentNumber() > 1) {
            installmentRepository.findByBooking_IdAndStatus(
                            booking.getId(),
                            InstallmentStatus.PENDING
                    ).stream()
                    .filter(i -> i.getInstallmentNumber() < installment.getInstallmentNumber())
                    .findAny()
                    .ifPresent(i -> {
                        throw new RuntimeException("Previous installment not paid");
                    });
        }

        // Create Razorpay order for THIS installment only
        int amountInPaise = (int) Math.round(installment.getAmount() * 100);

        RazorpayOrderResponse order =
                razorpayService.createOrder(amountInPaise, booking.getId());

        installment.setRazorpayOrderId(order.getOrderId());
        installmentRepository.save(installment);

        // Track payment (important for admin + webhook)
        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(installment.getAmount());
        payment.setGatewayOrderId(order.getOrderId());
        payment.setStatus("CREATED");
        paymentRepository.save(payment);

        InstallmentPayResponse res = new InstallmentPayResponse();
        res.setBookingId(booking.getId());
        res.setInstallmentId(installment.getId());
        res.setRazorpayOrderId(order.getOrderId());
        res.setAmount(amountInPaise);

        return res;
    }

    @Override
    public List<InstallmentResponse> getInstallmentsByBooking(
            Long bookingId,
            Long userId
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // SECURITY CHECK (VERY IMPORTANT)
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        List<Installment> installments =
                installmentRepository.findByBookingIdOrderByInstallmentNumber(bookingId);

        return installments.stream()
                .map(i -> new InstallmentResponse(
                        i.getId(),
                        i.getInstallmentNumber(),
                        i.getAmount(),
                        i.getStatus(),
                        i.getPaidAt()
                ))
                .collect(Collectors.toList());
    }


}
