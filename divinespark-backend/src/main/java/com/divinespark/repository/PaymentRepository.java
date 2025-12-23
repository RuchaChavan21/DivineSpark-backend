package com.divinespark.repository;

import com.divinespark.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByGatewayOrderId(String gatewayOrderId);

}
