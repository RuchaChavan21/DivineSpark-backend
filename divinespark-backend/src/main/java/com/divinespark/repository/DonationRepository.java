package com.divinespark.repository;

import com.divinespark.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    Donation findByRazorpayOrderId(String orderId);

    List<Donation> findAllByOrderByCreatedAtDesc();

    // ✅ Total donated amount
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'SUCCESS'")
    double getTotalDonatedAmount();

    // ✅ Unique donors count
    @Query("SELECT COUNT(DISTINCT d.userId) FROM Donation d WHERE d.status = 'SUCCESS'")
    long getTotalDonors();

    // ✅ Monthly donations (month, year, amount)
    @Query("""
        SELECT MONTH(d.createdAt), YEAR(d.createdAt), SUM(d.amount)
        FROM Donation d
        WHERE d.status = 'SUCCESS'
        GROUP BY YEAR(d.createdAt), MONTH(d.createdAt)
        ORDER BY YEAR(d.createdAt), MONTH(d.createdAt)
    """)
    List<Object[]> getMonthlyDonations();
}
