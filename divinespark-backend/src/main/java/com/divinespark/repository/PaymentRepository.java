package com.divinespark.repository;

import com.divinespark.dto.AdminPaymentResponse;
import com.divinespark.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByGatewayOrderId(String gatewayOrderId);

    Optional<Payment> findTopByBookingIdOrderByCreatedAtDesc(Long bookingId);

    @Query("""
SELECT new com.divinespark.dto.AdminPaymentResponse(
    p.id,
    u.email,
    s.title,
    p.amount,
    p.status,
    p.gatewayOrderId,
    p.createdAt
)
FROM Payment p
JOIN Booking b ON b.id = p.bookingId
JOIN b.user u
JOIN b.session s
ORDER BY p.createdAt DESC
""")
    List<AdminPaymentResponse> fetchAdminPayments();


}
