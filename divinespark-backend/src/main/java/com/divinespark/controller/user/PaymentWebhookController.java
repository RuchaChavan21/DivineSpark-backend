package com.divinespark.controller.user;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.service.PaymentService;
import com.divinespark.utils.RazorpaySignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

        public PaymentWebhookController(
            PaymentService paymentService,
            ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature,
            HttpServletRequest request) throws Exception {

        boolean isValid = RazorpaySignatureUtil.verify(
                payload,
                signature,
                webhookSecret
        );

        if (!isValid) {
            return ResponseEntity.status(401).build();
        }

        JsonNode root = objectMapper.readTree(payload);
        String event = root.get("event").asText();

        if ("payment.captured".equals(event)) {

            String orderId =
                    root.at("/payload/payment/entity/order_id").asText();

            PaymentCallbackRequest req = new PaymentCallbackRequest();
            req.setGatewayOrderId(orderId);
            req.setPaymentStatus("SUCCESS");

            paymentService.handlePaymentCallback(req);
        }

        if ("payment.failed".equals(event)) {

            String orderId =
                    root.at("/payload/payment/entity/order_id").asText();

            PaymentCallbackRequest req = new PaymentCallbackRequest();
            req.setGatewayOrderId(orderId);
            req.setPaymentStatus("FAILED");

            paymentService.handlePaymentCallback(req);
        }

        return ResponseEntity.ok().build();
    }
}
