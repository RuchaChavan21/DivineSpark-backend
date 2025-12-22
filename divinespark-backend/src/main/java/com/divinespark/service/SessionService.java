package com.divinespark.service;

import com.divinespark.dto.SessionCreateRequest;
import com.divinespark.dto.SessionDetailResponse;
import com.divinespark.dto.SessionUpdateRequest;
import com.divinespark.dto.SessionUserListResponse;
import com.divinespark.entity.Session;
import org.springframework.data.domain.Page;

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

}
