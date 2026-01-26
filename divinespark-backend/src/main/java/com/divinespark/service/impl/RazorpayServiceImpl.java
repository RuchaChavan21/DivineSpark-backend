package com.divinespark.service.impl;

import com.divinespark.config.RazorpayConfig;
import com.divinespark.dto.RazorpayOrderResponse;
import com.divinespark.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayConfig razorpayConfig;

    public RazorpayServiceImpl(RazorpayConfig razorpayConfig) {
        this.razorpayConfig = razorpayConfig;
    }

    @Override
    public RazorpayOrderResponse createOrder(int amountInPaise, Long referenceId) {

        try {
            RazorpayClient client = new RazorpayClient(
                    razorpayConfig.getKeyId(),
                    razorpayConfig.getKeySecret()
            );

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise); // already paise
            orderRequest.put("currency", razorpayConfig.getCurrency());
            orderRequest.put("receipt", "ref_" + referenceId);
            orderRequest.put("payment_capture", 1);

            Order order = client.orders.create(orderRequest);

            RazorpayOrderResponse response = new RazorpayOrderResponse();
            response.setOrderId(order.get("id"));

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }

}
