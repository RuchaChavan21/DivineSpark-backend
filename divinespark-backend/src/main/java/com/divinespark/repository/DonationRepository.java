package com.divinespark.repository;

import com.divinespark.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    Donation findByRazorpayOrderId(String orderId);

    List<Donation> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'SUCCESS'")
    double getTotalDonatedAmount();
}
