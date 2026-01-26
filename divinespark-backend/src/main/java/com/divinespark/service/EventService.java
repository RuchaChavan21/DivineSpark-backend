package com.divinespark.service;

import com.divinespark.dto.EventRequest;
import com.divinespark.dto.EventTickerResponse;
import com.divinespark.entity.Event;

import java.util.List;

public interface EventService {

    Event create(EventRequest request);

    Event update(Long id, EventRequest request);

    void delete(Long id);

    List<Event> getAllAdmin();

    List<EventTickerResponse> getTickerEvents();
}
