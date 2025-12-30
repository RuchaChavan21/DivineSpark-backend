package com.divinespark.service;

import com.divinespark.dto.PaymentCallbackRequest;

public interface PaymentService {

    void handlePaymentCallback(PaymentCallbackRequest request);

    public void handlePaymentFailure(String gatewayOrderId);

}
