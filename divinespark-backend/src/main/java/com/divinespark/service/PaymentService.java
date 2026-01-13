package com.divinespark.service;

import com.divinespark.dto.AdminPaymentResponse;

import java.util.List;

public interface PaymentService {


    public void handlePaymentFailure(String gatewayOrderId);

    List<AdminPaymentResponse> getAllPaymentsForAdmin();

    boolean handlePaymentCaptured(String razorpayOrderId, int amount);

}
