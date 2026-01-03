package com.divinespark.service;

import com.divinespark.dto.*;
import com.divinespark.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SessionService {
    Session create(SessionCreateRequest req);
    Session update(Long id, SessionUpdateRequest req);
    void delete(Long id);
    Page<Session> getAll(int page, int size);
    SessionUserListResponse getUpcomingSessions(
            int page,
            int size,
            String type
    );
    SessionDetailResponse getSessionDetails(Long sessionId);
    void joinFreeSession(Long sessionId, Long userId);

    PaymentInitiateResponse initiatePaidSession(
            Long sessionId,
            Long userId
    );

    List<AdminSessionUserResponse> getUsersBySession(Long sessionId);

    List<AdminSessionBookingResponse> getBookingsBySession(Long sessionId);

    void updateStatus(Long sessionId, String status);


    void uploadResource(Long sessionId, String fileType, MultipartFile file);

    Page<Session> getPastSessions(int page, int size);

    long getUpcomingSessionCount();

    long getPastSessionCount();

}
