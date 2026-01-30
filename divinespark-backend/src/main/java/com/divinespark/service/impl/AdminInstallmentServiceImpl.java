package com.divinespark.service.impl;

import com.divinespark.dto.AdminInstallmentResponse;
import com.divinespark.dto.AdminSessionUserInstallmentResponse;
import com.divinespark.entity.Booking;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.InstallmentRepository;
import com.divinespark.service.AdminInstallmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminInstallmentServiceImpl implements AdminInstallmentService {

    private final BookingRepository bookingRepository;
    private final InstallmentRepository installmentRepository;

    public AdminInstallmentServiceImpl(
            BookingRepository bookingRepository,
            InstallmentRepository installmentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.installmentRepository = installmentRepository;
    }

    @Override
    public List<AdminSessionUserInstallmentResponse> getSessionInstallments(Long sessionId) {

        List<Booking> bookings =
                bookingRepository.findBookingEntitiesBySessionId(sessionId);


        return bookings.stream().map(booking -> {

            List<AdminInstallmentResponse> installments =
                    installmentRepository
                            .findByBookingIdOrderByInstallmentNumber(booking.getId())
                            .stream()
                            .map(i -> new AdminInstallmentResponse(
                                    i.getId(),
                                    i.getInstallmentNumber(),
                                    i.getAmount(),
                                    i.getStatus(),
                                    i.getPaidAt()
                            ))
                            .collect(Collectors.toList());

            return new AdminSessionUserInstallmentResponse(
                    booking.getId(),
                    booking.getUser().getId(),
                    booking.getUser().getUsername(),
                    booking.getUser().getEmail(),
                    booking.getUser().getContactNumber(),
                    booking.getTotalAmount(),
                    booking.getPaidAmount(),
                    booking.getRemainingAmount(),
                    booking.getBookingStatus(),
                    installments
            );
        }).collect(Collectors.toList());
    }
}
