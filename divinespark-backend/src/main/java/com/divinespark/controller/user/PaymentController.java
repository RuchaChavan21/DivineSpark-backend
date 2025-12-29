package com.divinespark.controller.user;

import com.divinespark.dto.PaymentCallbackRequest;
import com.divinespark.dto.PaymentInitiateRequest;
import com.divinespark.dto.PaymentInitiateResponse;
import com.divinespark.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(
            @RequestBody PaymentCallbackRequest request) {

        paymentService.handlePaymentCallback(request);
        return ResponseEntity.ok("Payment processed");
    }

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @RequestBody PaymentInitiateRequest request,
            Authentication authentication) {

        String email = authentication.getName(); // logged-in user
        return ResponseEntity.ok(
                paymentService.initiatePayment(request.getSessionId(), email)
        );
    }


}

