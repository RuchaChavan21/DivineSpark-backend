package com.divinespark.repository;

import com.divinespark.entity.Session;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Page<Session> findByStatus(SessionStatus status, Pageable pageable);

    Page<Session> findByStatusAndType(SessionStatus status, SessionType type, Pageable pageable);
}
