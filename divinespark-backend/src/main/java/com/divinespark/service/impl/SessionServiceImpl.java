package com.divinespark.service.impl;

import com.divinespark.dto.SessionCreateRequest;
import com.divinespark.dto.SessionUpdateRequest;

import com.divinespark.entity.Session;
import com.divinespark.repository.SessionRepository;
import com.divinespark.service.SessionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repo;

    public SessionServiceImpl(SessionRepository repo) {
        this.repo = repo;
    }

    @Override
    public Session create(SessionCreateRequest req) {
        Session s = new Session();
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setType(req.getType());
        s.setPrice(req.getPrice());
        s.setFreeZoomLink(req.getFreeZoomLink());
        s.setPaidZoomLink(req.getPaidZoomLink());
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());
        s.setMaxSeats(req.getMaxSeats());
        s.setGuideName(req.getGuideName());
        return repo.save(s);
    }

    @Override
    public Session update(Long id, SessionUpdateRequest req) {
        Session s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (req.getTitle() != null) s.setTitle(req.getTitle());
        if (req.getDescription() != null) s.setDescription(req.getDescription());
        if (req.getType() != null) s.setType(req.getType());
        if (req.getPrice() != null) s.setPrice(req.getPrice());
        if (req.getFreeZoomLink() != null) s.setFreeZoomLink(req.getFreeZoomLink());
        if (req.getPaidZoomLink() != null) s.setPaidZoomLink(req.getPaidZoomLink());
        if (req.getStartTime() != null) s.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) s.setEndTime(req.getEndTime());
        if (req.getMaxSeats() != null) {
            s.setMaxSeats(req.getMaxSeats());
            // reset availability if seats increased
            s.setAvailableSeats(req.getMaxSeats());
        }
        if (req.getGuideName() != null) s.setGuideName(req.getGuideName());

        return repo.save(s);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Session not found");
        }
        repo.deleteById(id);
    }

    @Override
    public Page<Session> getAll(int page, int size) {
        PageRequest req = PageRequest.of(page, size);
        return repo.findAll(req);
    }



}
