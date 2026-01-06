package com.divinespark.utils;

import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class RazorpaySignatureUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static boolean verify(
            String payload,
            String actualSignature,
            String webhookSecret) {

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec key =
                    new SecretKeySpec(webhookSecret.getBytes(), HMAC_SHA256);
            mac.init(key);

            byte[] hash = mac.doFinal(payload.getBytes());
            String expectedSignature =
                    Base64.getEncoder().encodeToString(hash);

            return expectedSignature.equals(actualSignature);

        } catch (Exception e) {
            return false;
        }
    }
}
