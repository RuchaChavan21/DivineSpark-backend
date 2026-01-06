package com.divinespark.service;

import com.divinespark.dto.AdminPaymentResponse;
import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.PaymentInitiateResponse;

import java.util.List;

public interface PaymentService {

    void handlePaymentCallback(PaymentCallbackRequest request);

    public void handlePaymentFailure(String gatewayOrderId);

    List<AdminPaymentResponse> getAllPaymentsForAdmin();

    void handlePaymentCaptured(String razorpayOrderId, int amount);

}
