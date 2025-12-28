package com.divinespark.service;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.PaymentInitiateResponse;

public interface PaymentService {

    void handlePaymentCallback(PaymentCallbackRequest request);
    PaymentInitiateResponse initiatePayment(Long sessionId, String userEmail);


}
