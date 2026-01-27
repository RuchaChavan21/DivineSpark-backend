package com.divinespark.service;

import com.divinespark.dto.RazorpayOrderResponse;

public interface RazorpayService {
    RazorpayOrderResponse createOrder(int amount, Long bookingId);
}
