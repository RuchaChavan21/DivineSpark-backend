package com.divinespark.repository;

import java.util.List;
import java.util.Optional;

import com.divinespark.entity.Installment;
import com.divinespark.entity.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByBookingIdOrderByInstallmentNumber(Long bookingId);

    Optional<Installment> findByBookingIdAndInstallmentNumber(Long bookingId, InstallmentStatus status);

    boolean existsByBookingIdAndStatus(Long bookingId, InstallmentStatus status);

    Optional<Installment> findByRazorpayOrderId(String razorpayOrderId);


    List<Installment> findByBooking_IdAndStatus(
            Long bookingId,
            InstallmentStatus status
    );

    @Modifying
    @Query("""
    update Installment i
    set i.razorpayOrderId = :orderId
    where i.id = :id
""")
    void updateOrderId(
            @Param("id") Long id,
            @Param("orderId") String orderId
    );

    @Modifying
    @Query("delete from Installment i where i.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);


}