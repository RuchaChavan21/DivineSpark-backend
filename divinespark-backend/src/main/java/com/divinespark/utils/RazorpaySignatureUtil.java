package com.divinespark.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class RazorpaySignatureUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public static boolean verify(
            String payload,
            String actualSignature,
            String secret) {

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
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
