package com.divinespark.service.impl;

import com.divinespark.dto.EventRequest;
import com.divinespark.dto.EventTickerResponse;
import com.divinespark.entity.Event;
import com.divinespark.repository.EventRepository;
import com.divinespark.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    public EventServiceImpl(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public Event create(EventRequest request) {
        Event event = new Event();
        mapRequest(event, request);
        return repository.save(event);
    }

    @Override
    public Event update(Long id, EventRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workshop/Event not found"));

        mapRequest(event, request);
        return repository.save(event);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllAdmin() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventTickerResponse> getTickerEvents() {

        OffsetDateTime now = OffsetDateTime.now();

        return repository.findAll().stream()
                .filter(event ->
                        event.getStartTime()
                                .plusMinutes(event.getDurationMinutes())
                                .isAfter(now)
                )
                .sorted(Comparator.comparing(Event::getStartTime))
                .limit(10)
                .map(this::mapToTicker)
                .toList();
    }

    private void mapRequest(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setDurationMinutes(request.getDurationMinutes());
    }

    private EventTickerResponse mapToTicker(Event event) {
        EventTickerResponse dto = new EventTickerResponse();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setDurationMinutes(event.getDurationMinutes());
        return dto;
    }
}
