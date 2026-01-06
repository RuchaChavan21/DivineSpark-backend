package com.divinespark.controller.user;

import com.divinespark.service.PaymentService;
import com.divinespark.utils.RazorpaySignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/payments")
public class PaymentWebhookController {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentWebhookController.class);

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final RazorpaySignatureUtil razorpaySignatureUtil;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public PaymentWebhookController(
            PaymentService paymentService,
            ObjectMapper objectMapper,
            RazorpaySignatureUtil razorpaySignatureUtil) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
        this.razorpaySignatureUtil = razorpaySignatureUtil;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {


        try {
            razorpaySignatureUtil.verify(payload, signature, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.get("event").asText();

            if ("payment.captured".equals(event)) {

                JsonNode payment = root
                        .path("payload")
                        .path("payment")
                        .path("entity");

                String orderId = payment.get("order_id").asText();
                int amount = payment.get("amount").asInt();

                paymentService.handlePaymentCaptured(orderId, amount);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }


}
