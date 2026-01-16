package com.divinespark.service;

import com.divinespark.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class SessionStatusScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(SessionStatusScheduler.class);

    private final SessionRepository sessionRepository;

    // Constructor Injection (best practice)
    public SessionStatusScheduler(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 5 * 60 * 1000) // every 5 minutes
    public void autoCompleteSessions() {

        OffsetDateTime now = OffsetDateTime.now();

        int updated = sessionRepository.markCompletedSessions(now);

        if (updated > 0) {
            log.info("Marked {} sessions as COMPLETED", updated);
        }
    }
}
