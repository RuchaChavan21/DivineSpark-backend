package com.divinespark.service.impl;

import com.divinespark.dto.*;
import com.divinespark.entity.*;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import com.divinespark.repository.*;
import com.divinespark.service.EmailService;
import com.divinespark.service.SessionService;
import com.divinespark.service.StorageService;

import com.divinespark.service.ZoomService;
import com.divinespark.utils.ZoomNameUtil;
import com.divinespark.utils.ZoomUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final ZoomService zoomService;
    private final SessionRepository sessionRepository;


    public SessionServiceImpl(
            SessionRepository repo,
            BookingRepository bookingRepository,
            UserRepository userRepo,
            EmailService emailService,
            PaymentRepository paymentRepository,
            StorageService storageService,
            SessionResourceRepository sessionResourceRepository,
            ZoomService zoomService, SessionRepository sessionRepository) {

        this.repo = repo;
        this.bookingRepository = bookingRepository;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.paymentRepository = paymentRepository;
        this.storageService = storageService;
        this.sessionResourceRepository = sessionResourceRepository;
        this.zoomService = zoomService;
        this.sessionRepository = sessionRepository;
    }

    // ================= ADMIN =================

    @Override
    public Session create(SessionCreateRequest req) {
        Session s = new Session();
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setType(req.getType());
        s.setPrice(req.getPrice());
        s.setZoomLink(req.getZoomLink());
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());
        s.setMaxSeats(req.getMaxSeats());
        s.setGuideName(req.getGuideName());
        s.setZoomMeetingId(ZoomUtils.extractMeetingId(req.getZoomLink()));
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
        if (req.getZoomLink() != null) s.setZoomLink(req.getZoomLink());
        if (req.getStartTime() != null) s.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) s.setEndTime(req.getEndTime());
        if (req.getMaxSeats() != null) {
            s.setMaxSeats(req.getMaxSeats());
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
    @Transactional
    public void joinFreeSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getType().name().equals("FREE")) {
            throw new RuntimeException("Paid session cannot be joined here");
        }

        if (!session.getStatus().name().equals("UPCOMING")) {
            throw new RuntimeException("Session not available");
        }

        if (session.getAvailableSeats().get() <= 0) {
            throw new RuntimeException("No seats available");
        }

        if (bookingRepository.existsByUserIdAndSessionIdAndStatus(
                userId, sessionId, "CONFIRMED")) {
            throw new RuntimeException("Already booked");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(user);
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        String firstName = ZoomNameUtil.getFirstName(user.getUsername());
        String lastName = ZoomNameUtil.getLastName();

        ZoomRegistrationResponse zoomResponse =
                zoomService.registerUser(
                        session.getZoomMeetingId(),
                        user.getEmail(),
                        firstName,
                        lastName
                );

        booking.setZoomRegistrantId(zoomResponse.getRegistrantId());
        booking.setZoomJoinUrl(zoomResponse.getJoinUrl());
        bookingRepository.save(booking);

        session.setAvailableSeats(session.getAvailableSeats().decrementAndGet());

        emailService.sendSessionJoinLink(
                user.getEmail(),
                session.getTitle(),
                booking.getZoomJoinUrl(),
                session.getGuideName(),
                session.getStartTime().toString(),
                session.getEndTime().toString(),
                "FREE"
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
    @Transactional(readOnly = true)
    public SessionUserListResponse getUpcomingSessions(int page, int size, String type) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Session> sessions;

        if (type != null) {
            SessionType sessionType = SessionType.valueOf(type.toUpperCase());
            sessions = repo.findByStatusAndType(
                    SessionStatus.UPCOMING,
                    sessionType,
                    pageRequest
            );
        } else {
            sessions = repo.findByStatus(
                    SessionStatus.UPCOMING,
                    pageRequest
            );
        }

        List<SessionUserResponse> sessionList = new ArrayList<>();

        for (Session session : sessions.getContent()) {
            SessionUserResponse dto = new SessionUserResponse();
            dto.setId(session.getId());
            dto.setTitle(session.getTitle());
            dto.setDescription(session.getDescription());
            dto.setType(session.getType());
            dto.setPrice(session.getPrice());
            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());
            dto.setGuideName(session.getGuideName());
            dto.setAvailableSeats(session.getAvailableSeats().get());
            sessionList.add(dto);
        }

        SessionUserListResponse response = new SessionUserListResponse();
        response.setSessions(sessionList);
        response.setTotalPages(sessions.getTotalPages());
        response.setTotalElements(sessions.getTotalElements());

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public SessionDetailResponse getSessionDetails(Long sessionId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionDetailResponse response = new SessionDetailResponse();
        response.setId(session.getId());
        response.setTitle(session.getTitle());
        response.setDescription(session.getDescription());
        response.setType(session.getType());
        response.setPrice(session.getPrice());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setTrainerName(session.getGuideName());
        response.setMaxSeats(session.getMaxSeats().get());
        response.setAvailableSeats(session.getAvailableSeats().get());
        response.setStatus(session.getStatus());

        return response;
    }


    @Override
    @Transactional
    public PaymentInitiateResponse initiatePaidSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getType() != SessionType.PAID) {
            throw new RuntimeException("This session is not paid");
        }

        if (session.getAvailableSeats().get() <= 0) {
            throw new RuntimeException("No seats available");
        }

        //Block ONLY confirmed booking
        if (bookingRepository.existsByUserIdAndSessionIdAndStatus(
                userId, sessionId, "CONFIRMED")) {
            throw new RuntimeException("Already booked");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository
                .findByUserIdAndSessionIdAndStatus(userId, sessionId, "PENDING")
                .orElseGet(() -> {
                    Booking b = new Booking();
                    b.setSession(session);
                    b.setUser(user);
                    b.setStatus("PENDING");
                    return bookingRepository.save(b);
                });

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(session.getPrice());
        payment.setStatus("CREATED");
        paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();
        response.setBookingId(booking.getId());
        response.setAmount(session.getPrice());

        return response;
    }

    @Override
    public List<AdminSessionUserResponse> getUsersBySession(Long sessionId) {
        return bookingRepository.findUsersBySessionId(sessionId);
    }

    @Override
    public List<AdminSessionBookingResponse> getBookingsBySession(Long sessionId) {
        return bookingRepository.findBookingsBySessionId(sessionId);
    }
    @Override
    public Page<Session> getPastSessions(int page, int size) {
        return repo.findPastSessions(
                LocalDateTime.now(),
                PageRequest.of(page, size)
        );
    }

    @Override
    public long getUpcomingSessionCount() {
        return sessionRepository.countByStatus(SessionStatus.UPCOMING);

    }

    @Override
    public long getPastSessionCount() {
        return sessionRepository.countByStatus(SessionStatus.COMPLETED);

    }

}
