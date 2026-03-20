package com.divinespark.service.impl;


import com.divinespark.dto.InstallmentPayResponse;
import com.divinespark.dto.InstallmentResponse;
import com.divinespark.dto.RazorpayOrderResponse;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Installment;
import com.divinespark.entity.Payment;
import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.entity.enums.InstallmentStatus;
import com.divinespark.entity.enums.PaymentType;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.InstallmentRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.InstallementService;
import com.divinespark.service.RazorpayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class InstallmentServiceImpl implements InstallementService {

    private final InstallmentRepository installmentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final UserRepository userRepository;

    public InstallmentServiceImpl(
            InstallmentRepository installmentRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            RazorpayService razorpayService,
            UserRepository userRepository
    ) {
        this.installmentRepository = installmentRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
        this.userRepository = userRepository;
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



    @Override
    @Transactional
    public boolean verifyAndMarkInstallmentPaid(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {

        // 1 Find installment by Razorpay order ID
        Installment installment = installmentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Installment not found"));

        Booking booking = installment.getBooking();

        // 2 Verify Razorpay signature
        System.out.println(">>> VERIFY SERVICE CALLED <<<");
        boolean verified = razorpayService.verifySignature(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        System.out.println("SIGNATURE VERIFIED = " + verified);


        if (!verified) {
            throw new RuntimeException("Payment verification failed");
        }

        // 3 Idempotency (avoid double update)
        if (installment.getStatus() == InstallmentStatus.PAID) {
            return true;
        }

        // 4 Mark FIRST installment as PAID
        installment.setStatus(InstallmentStatus.PAID);
        installment.setPaidAt(OffsetDateTime.now());
        installmentRepository.save(installment);

        // 5️ Update booking amounts
        booking.setPaidAmount(
                booking.getPaidAmount() + installment.getAmount()
        );

        booking.setRemainingAmount(
                booking.getTotalAmount() - booking.getPaidAmount()
        );

        // 6️ Update booking status
        booking.setBookingStatus(BookingStatus.PARTIALLY_PAID);
        bookingRepository.save(booking);

        // 7️ Update payment record
        paymentRepository.findByGatewayOrderId(razorpayOrderId)
                .ifPresent(p -> {
                    p.setStatus("SUCCESS");
                    paymentRepository.save(p);
                });

        return true;
    }





}