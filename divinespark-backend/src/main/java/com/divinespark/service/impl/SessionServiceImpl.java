package com.divinespark.service.impl;

import com.divinespark.dto.*;

import com.divinespark.entity.Booking;
import com.divinespark.entity.Payment;
import com.divinespark.entity.Session;
import com.divinespark.entity.enums.SessionStatus;
import com.divinespark.entity.enums.SessionType;
import com.divinespark.repository.BookingRepository;
import com.divinespark.repository.PaymentRepository;
import com.divinespark.repository.SessionRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.EmailService;
import com.divinespark.service.SessionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final PaymentRepository paymentRepository;

    public SessionServiceImpl(
            SessionRepository repo,
            BookingRepository bookingRepo,
            UserRepository userRepo,
            EmailService emailService,
            PaymentRepository paymentRepository) {
        this.repo = repo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.paymentRepository = paymentRepository;
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

    @Override
    public SessionUserListResponse getUpcomingSessions(int page, int size, String type) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<Session> result;

        SessionType sessionType = type != null ? SessionType.from(type) : null;

        if (sessionType != null) {
            result = repo.findByStatusAndType(
                    SessionStatus.UPCOMING,
                    sessionType,
                    pageable
            );
        } else {
            result = repo.findByStatus(SessionStatus.UPCOMING, pageable);
        }

        List<SessionUserResponse> list = new ArrayList<>();
        for (Session s : result.getContent()) {
            SessionUserResponse r = new SessionUserResponse();
            r.setId(s.getId());
            r.setTitle(s.getTitle());
            r.setDescription(s.getDescription());
            r.setType(s.getType().name());
            r.setPrice(s.getPrice());
            r.setStartTime(s.getStartTime());
            r.setEndTime(s.getEndTime());
            r.setGuideName(s.getGuideName());
            list.add(r);
        }

        SessionUserListResponse response = new SessionUserListResponse();
        response.setSessions(list);
        response.setPage(result.getNumber());
        response.setSize(result.getSize());
        response.setTotalElements(result.getTotalElements());
        response.setTotalPages(result.getTotalPages());

        return response;
    }

    @Override
    public SessionDetailResponse getSessionDetails(Long sessionId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionDetailResponse res = new SessionDetailResponse();
        res.setId(session.getId());
        res.setTitle(session.getTitle());
        res.setDescription(session.getDescription());
        res.setType(session.getType().name());
        res.setPrice(session.getPrice());
        res.setTrainerName(session.getGuideName());
        res.setStartTime(session.getStartTime());
        res.setEndTime(session.getEndTime());
        res.setMaxSeats(session.getMaxSeats());
        res.setAvailableSeats(session.getAvailableSeats());
        res.setStatus(session.getStatus().name());

        return res;
    }

    @Override
    @Transactional
    public void joinFreeSession(Long sessionId, Long userId) {

        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getType().name().equals("FREE")) {
            throw new RuntimeException("Paid session cannot be joined directly");
        }

        if (!session.getStatus().name().equals("UPCOMING")) {
            throw new RuntimeException("Session not available");
        }

        if (session.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        if (bookingRepo.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new RuntimeException("Already joined");
        }

        // Reduce seat count (atomic inside TX)
        session.setAvailableSeats(session.getAvailableSeats() - 1);

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(userRepo.findById(userId).orElseThrow());
        booking.setStatus("CONFIRMED");

        bookingRepo.save(booking);

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
    @Transactional
    public PaymentInitiateResponse initiatePaidSession(Long sessionId, Long userId) {
        Session session = repo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if(!session.getType().name().equals("PAID")) {
            throw new RuntimeException("Free session does not require payment");
        }

        if(!session.getStatus().name().equals("UPCOMING")) {
            throw new RuntimeException("Session not available");
        }

        if(session.getAvailableSeats() <= 0) {
            throw new RuntimeException("No seats available");
        }

        if(bookingRepo.existsByUserIdAndSessionId(userId, sessionId)) {
            throw new RuntimeException("Already booked, please check your email to join session");
        }

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(userRepo.findById(userId).orElseThrow());
        booking.setStatus("PENDING");
        bookingRepo.save(booking);

        // Simulate payment gateway order creation
        String fakeOrderId = "ORDER_" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setBookingId(booking.getId());
        payment.setAmount(session.getPrice());
        payment.setStatus("CREATED");
        payment.setGatewayOrderId(fakeOrderId);
        paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();
        response.setBookingId(booking.getId());
        response.setOrderId(fakeOrderId);
        response.setAmount(session.getPrice());
        response.setCurrency("INR");

        return response;
    }



}
