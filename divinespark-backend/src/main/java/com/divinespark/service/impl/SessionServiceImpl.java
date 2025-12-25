package com.divinespark.service.impl;

import com.divinespark.dto.*;
import com.divinespark.entity.Booking;
import com.divinespark.entity.Session;
import com.divinespark.entity.SessionResource;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import com.divinespark.repository.*;
import com.divinespark.service.EmailService;
import com.divinespark.service.SessionService;
import com.divinespark.service.StorageService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SessionServiceImpl implements SessionService  {

    private final SessionRepository repo;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;
    private final StorageService storageService;
    private final SessionResourceRepository sessionResourceRepository;

    public SessionServiceImpl(
            SessionRepository repo,
            BookingRepository bookingRepository,
            UserRepository userRepo,
            EmailService emailService,
            PaymentRepository paymentRepository,
            StorageService storageService,
            SessionResourceRepository sessionResourceRepository) {

        this.repo = repo;
        this.bookingRepository = bookingRepository;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.paymentRepository = paymentRepository;
        this.storageService = storageService;
        this.sessionResourceRepository = sessionResourceRepository;
    }

    // ================= ADMIN =================

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
            s.setAvailableSeats(req.getMaxSeats());
        }
        if (req.getGuideName() != null) s.setGuideName(req.getGuideName());

        return repo.save(s);
    }

    // ✅ FIXED: Missing method
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Session not found");
        }
        repo.deleteById(id);
    }

    @Override
    public void updateStatus(Long sessionId, String status) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionStatus newStatus;
        try {
            newStatus = SessionStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid session status");
        }

        if (session.getStatus() == SessionStatus.COMPLETED ||
                session.getStatus() == SessionStatus.CANCELLED) {
            throw new RuntimeException("Session status cannot be changed");
        }

        session.setStatus(newStatus);
        repo.save(session);
    }

    // ================= USER =================

    @Override
    public void joinFreeSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getType() != SessionType.FREE)
            throw new RuntimeException("Paid session cannot be joined directly");

        if (session.getStatus() != SessionStatus.UPCOMING)
            throw new RuntimeException("Session not available");

        if (session.getAvailableSeats() <= 0)
            throw new RuntimeException("No seats available");

        if (bookingRepository.existsByUserIdAndSessionId(userId, sessionId))
            throw new RuntimeException("Already joined");

        session.setAvailableSeats(session.getAvailableSeats() - 1);

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(userRepo.findById(userId).orElseThrow());
        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);

        emailService.sendFreeSessionLink(
                booking.getUser().getEmail(),
                session.getTitle(),
                session.getFreeZoomLink(),
                session.getGuideName(),
                session.getStartTime().toString(),
                session.getEndTime().toString()
        );
    }

    @Override
    public void uploadResource(Long sessionId, String fileType, MultipartFile file) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        String url = storageService.upload(file, "sessions/" + sessionId);

        SessionResource resource = new SessionResource();
        resource.setSession(session);
        resource.setFileName(file.getOriginalFilename());
        resource.setFileUrl(url);
        resource.setFileType(fileType);

        sessionResourceRepository.save(resource);
    }


    @Override
    public Page<Session> getAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }

    @Override
    public SessionUserListResponse getUpcomingSessions(int page, int size, String type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SessionDetailResponse getSessionDetails(Long sessionId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PaymentInitiateResponse initiatePaidSession(Long sessionId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<AdminSessionUserResponse> getUsersBySession(Long sessionId) {
        return bookingRepository.findUsersBySessionId(sessionId);
    }

    @Override
    public List<AdminSessionBookingResponse> getBookingsBySession(Long sessionId) {
        return bookingRepository.findBookingsBySessionId(sessionId);
    }

}
