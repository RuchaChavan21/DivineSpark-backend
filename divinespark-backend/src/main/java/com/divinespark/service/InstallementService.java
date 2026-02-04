package com.divinespark.service;

import com.divinespark.dto.AdminSessionOverviewResponse;
import com.divinespark.dto.InstallmentPayResponse;
import com.divinespark.dto.InstallmentResponse;

import java.util.List;

public interface InstallementService {

    InstallmentPayResponse payInstallment(Long installmentId, Long userId);

    List<InstallmentResponse> getInstallmentsByBooking(Long bookingId, Long userId);

    boolean verifyAndMarkInstallmentPaid (String orderId,
                                          String paymentId,
                                          String signature);

}
