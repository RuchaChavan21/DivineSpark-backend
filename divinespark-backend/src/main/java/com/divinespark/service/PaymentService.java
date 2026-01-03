package com.divinespark.service;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.PaymentInitiateResponse;

public interface PaymentService {

    void handlePaymentCallback(PaymentCallbackRequest request);

    public void handlePaymentFailure(String gatewayOrderId);

}
