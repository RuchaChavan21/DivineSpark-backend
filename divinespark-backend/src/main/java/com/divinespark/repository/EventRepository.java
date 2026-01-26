package com.divinespark.repository;

import com.divinespark.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface EventRepository
        extends JpaRepository<Event, Long> {

    @Query("""
        SELECT w FROM Event w
        WHERE (w.startTime + (w.durationMinutes * 1 minute)) > :now
        ORDER BY w.startTime ASC
    """)
    List<Event> findActiveForTicker(@Param("now") OffsetDateTime now);

    List<Event> findAllByOrderByCreatedAtDesc();
}

