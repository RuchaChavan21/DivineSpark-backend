package com.divinespark.service;

import com.divinespark.dto.AdminSessionUserInstallmentResponse;
import java.util.List;

public interface AdminInstallmentService {
    List<AdminSessionUserInstallmentResponse> getSessionInstallments(Long sessionId);
}
