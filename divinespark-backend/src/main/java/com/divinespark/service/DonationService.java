package com.divinespark.service;

import com.divinespark.dto.*;

import java.util.List;

public interface DonationService {

    RazorpayOrderResponse createDonationOrder(
            Long userId,
            DonationCreateRequest request
    );

    void confirmDonationPayment(DonationVerifyRequest request);


    List<AdminDonationResponse> getAllDonations();

    double getTotalDonatedAmount();

    void handleDonationCaptured(String orderId, int amount);

}
