package com.divinespark.service.impl;

import com.divinespark.config.RazorpayConfig;
import com.divinespark.dto.RazorpayOrderResponse;
import com.divinespark.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayConfig razorpayConfig;
    @Value("${razorpay.key.secret}")
    private String razorpaySecret;


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

    @Override
    public boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            razorpaySecret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // 🔥 HEX encoding (NOT Base64)
            String generatedSignature = bytesToHex(hash);

            return generatedSignature.equals(razorpaySignature);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }


}
