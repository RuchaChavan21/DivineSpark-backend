package com.divinespark.controller.user;

import com.divinespark.service.InstallementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentVerificationController {

    private final InstallementService installmentService;

    public PaymentVerificationController(InstallementService installmentService) {
        this.installmentService = installmentService;
    }

    @PostMapping("/installment/verify")
    public ResponseEntity<Void> verifyInstallment(
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature
    ) {
        System.out.println(">>> VERIFY API HIT <<<");
        System.out.println("orderId = " + razorpayOrderId);
        System.out.println("paymentId = " + razorpayPaymentId);
        System.out.println("signature = " + razorpaySignature);

        installmentService.verifyAndMarkInstallmentPaid(
                razorpayOrderId,
                razorpayPaymentId,
                razorpaySignature
        );

        return ResponseEntity.ok().build();
    }

}