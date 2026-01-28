package com.divinespark.service;

import com.divinespark.dto.AdminSessionOverviewResponse;

public interface AdminSessionPaymentService {
    AdminSessionOverviewResponse getSessionOverview(Long sessionId);
}
