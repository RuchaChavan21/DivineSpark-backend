package com.divinespark.service.impl;

import com.divinespark.dto.*;
import com.divinespark.entity.*;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import com.divinespark.exception.BusinessException;
import com.divinespark.repository.*;
import com.divinespark.service.*;
import com.divinespark.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repo;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepo;
    private final PaymentRepository paymentRepository;
    private final SessionRepository sessionRepository;
    private final RazorpayService razorpayService;

    public SessionServiceImpl(
            SessionRepository repo,
            BookingRepository bookingRepository,
            UserRepository userRepo,
            PaymentRepository paymentRepository,
            SessionRepository sessionRepository,
            RazorpayService razorpayService) {

        this.repo = repo;
        this.bookingRepository = bookingRepository;
        this.userRepo = userRepo;
        this.paymentRepository = paymentRepository;
        this.sessionRepository = sessionRepository;
        this.razorpayService = razorpayService;
    }

    // ================= ADMIN =================

    @Override
    public Session create(SessionCreateRequest req) {

        if (req.getStatus() == SessionStatus.UPCOMING) {
            if (ValidationUtil.isBlank(req.getWhatsappGroupLink())) {
                throw new RuntimeException("WhatsApp group link is required for UPCOMING sessions");
            }
            if (!ValidationUtil.isValidWhatsAppGroupLink(req.getWhatsappGroupLink())) {
                throw new RuntimeException("Invalid WhatsApp group link");
            }
        }

        Session s = new Session();
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setType(req.getType());
        s.setPrice(req.getPrice());
        s.setWhatsLink(req.getWhatsappGroupLink());

        // OffsetDateTime comes directly from DTO
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());

        s.setMaxSeats(new AtomicInteger(req.getMaxSeats()));
        s.setAvailableSeats(new AtomicInteger(req.getMaxSeats()));

        s.setGuideName(req.getGuideName());
        s.setStatus(req.getStatus() != null ? req.getStatus() : SessionStatus.UPCOMING);

        s.setHasThumbnail(false);
        s.setThumbnailData(null);

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
        if (req.getStartTime() != null) s.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) s.setEndTime(req.getEndTime());

        if (req.getMaxSeats() != null) {
            s.setMaxSeats(new AtomicInteger(req.getMaxSeats()));
            s.setAvailableSeats(new AtomicInteger(req.getMaxSeats()));
        }

        if (req.getGuideName() != null) s.setGuideName(req.getGuideName());
        if (req.getWhatsappGroupLink() != null) s.setWhatsLink(req.getWhatsappGroupLink());
        if (req.getStatus() != null) s.setStatus(req.getStatus());

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
    public void joinFreeSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getType() != SessionType.FREE)
            throw new RuntimeException("Paid session cannot be joined");

        if (session.getStatus() != SessionStatus.UPCOMING)
            throw new RuntimeException("Session not available");

        if (session.getAvailableSeats().get() <= 0)
            throw new RuntimeException("No seats available");

        if (bookingRepository.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new BusinessException("You have already booked this session.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(user);
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        session.getAvailableSeats().decrementAndGet();
    }

    @Override
    public PaymentInitiateResponse initiatePaidSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getType() != SessionType.PAID)
            throw new RuntimeException("Not a paid session");

        if (session.getAvailableSeats().get() <= 0)
            throw new RuntimeException("No seats available");

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

        RazorpayOrderResponse order =
                razorpayService.createOrder(session.getPrice(), booking.getId());

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(session.getPrice());
        payment.setGatewayOrderId(order.getOrderId());
        payment.setStatus("CREATED");
        paymentRepository.save(payment);

        PaymentInitiateResponse res = new PaymentInitiateResponse();
        res.setBookingId(booking.getId());
        res.setOrderId(order.getOrderId());
        res.setAmount(session.getPrice() * 100);
        res.setCurrency("INR");

        return res;
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
    public Page<Session> getAll(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
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

    @Override
    public long getTotalSessionCount() {
        return sessionRepository.count();
    }

    @Override
    public SessionUserListResponse getUpcomingSessions(int page, int size, String type) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Session> sessions = (type != null)
                ? repo.findByStatusAndType(SessionStatus.UPCOMING, SessionType.valueOf(type), pageRequest)
                : repo.findByStatus(SessionStatus.UPCOMING, pageRequest);

        List<SessionUserResponse> list = new ArrayList<>();

        for (Session s : sessions.getContent()) {
            SessionUserResponse dto = new SessionUserResponse();
            dto.setId(s.getId());
            dto.setTitle(s.getTitle());
            dto.setDescription(s.getDescription());
            dto.setType(s.getType());
            dto.setPrice(s.getPrice());
            dto.setStartTime(s.getStartTime().toLocalDateTime());
            dto.setEndTime(s.getEndTime().toLocalDateTime());
            dto.setGuideName(s.getGuideName());
            dto.setAvailableSeats(s.getAvailableSeats().get());
            list.add(dto);
        }

        SessionUserListResponse res = new SessionUserListResponse();
        res.setSessions(list);
        res.setTotalPages(sessions.getTotalPages());
        res.setTotalElements(sessions.getTotalElements());
        return res;
    }

    @Override
    public SessionDetailResponse getSessionDetails(Long sessionId) {

        Session s = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionDetailResponse res = new SessionDetailResponse();
        res.setId(s.getId());
        res.setTitle(s.getTitle());
        res.setDescription(s.getDescription());
        res.setType(s.getType());
        res.setPrice(s.getPrice());
        res.setStartTime(s.getStartTime().toLocalDateTime());
        res.setEndTime(s.getEndTime().toLocalDateTime());
        res.setTrainerName(s.getGuideName());
        res.setMaxSeats(s.getMaxSeats().get());
        res.setAvailableSeats(s.getAvailableSeats().get());
        res.setStatus(s.getStatus());

        return res;
    }

    @Override
    public String getWhatsappLinkIfConfirmed(Long sessionId, Long userId) {

        // 1. Fetch CONFIRMED booking for user + session
        Booking booking = bookingRepository
                .findByUserIdAndSessionIdAndStatus(
                        userId,
                        sessionId,
                        "CONFIRMED"
                )
                .orElseThrow(() -> new ExpressionException(
                        "You are not allowed to access this session"
                ));

        // 2. Fetch session from booking (already mapped)
        String whatsappLink = booking
                .getSession()
                .getWhatsLink();

        // 3. Safety check (admin forgot to add link)
        if (whatsappLink == null || whatsappLink.isBlank()) {
            throw new IllegalStateException(
                    "WhatsApp group link not configured for this session"
            );
        }

        // 4. Return link
        return whatsappLink;
    }


}
