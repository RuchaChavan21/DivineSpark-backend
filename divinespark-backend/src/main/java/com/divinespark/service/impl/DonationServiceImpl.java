package com.divinespark.service.impl;

import com.divinespark.dto.*;
import com.divinespark.entity.Donation;
import com.divinespark.repository.DonationRepository;
import com.divinespark.repository.UserRepository;
import com.divinespark.service.DonationService;
import com.divinespark.service.RazorpayService;
import com.divinespark.utils.RazorpaySignatureUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final RazorpayService razorpayService;
    private final UserRepository userRepository;

    public DonationServiceImpl(
            DonationRepository donationRepository,
            RazorpayService razorpayService, UserRepository userRepository
    ) {
        this.donationRepository = donationRepository;
        this.razorpayService = razorpayService;
        this.userRepository = userRepository;
    }

    @Override
    public RazorpayOrderResponse createDonationOrder(
            Long userId,
            DonationCreateRequest request
    ) {

        RazorpayOrderResponse order =
                razorpayService.createOrder(request.getAmount(), null);

        Donation donation = new Donation();
        donation.setUserId(userId);
        donation.setAmount(request.getAmount());
        donation.setNote(request.getNote());
        donation.setStatus("CREATED");
        donation.setRazorpayOrderId(order.getOrderId());

        donationRepository.save(donation);
        return order;
    }

    @Override
    @Transactional
    public void confirmDonationPayment(DonationVerifyRequest request)
    {

        Donation donation = donationRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId());

        if (donation == null) {
            throw new RuntimeException("Donation not found");
        }

        String payload =
                request.getRazorpayOrderId() + "|" +
                        request.getRazorpayPaymentId();

        boolean valid = RazorpaySignatureUtil.verify(
                payload,
                request.getRazorpaySignature(),
                "<RAZORPAY_WEBHOOK_SECRET>"
        );

        if (!valid) {
            throw new RuntimeException("Invalid Razorpay signature");
        }

        donation.setStatus("SUCCESS");
        donation.setRazorpayPaymentId(request.getRazorpayPaymentId());
    }

    @Override
    public List<AdminDonationResponse> getAllDonations() {

        List<Donation> donations =
                donationRepository.findAllByOrderByCreatedAtDesc();

        List<AdminDonationResponse> response = new ArrayList<>();

        for (Donation d : donations) {
            AdminDonationResponse dto = new AdminDonationResponse();
            dto.setId(d.getId());
            dto.setUserId(d.getUserId());
            dto.setAmount(d.getAmount());
            dto.setNote(d.getNote());
            dto.setStatus(d.getStatus());
            dto.setCreatedAt(d.getCreatedAt());
            // 🔹 Fetch user
            if (d.getUserId() != null) {
                userRepository.findById(d.getUserId()).ifPresent(user -> {
                    dto.setUserName(user.getUsername()); // or getName()
                    dto.setContactNumber(user.getContactNumber());
                });
            }
            response.add(dto);
        }

        return response;
    }

    @Override
    public double getTotalDonatedAmount() {
        return donationRepository.getTotalDonatedAmount();
    }

    @Override
    @Transactional
    public void handleDonationCaptured(String orderId, int amount) {

        Donation donation =
                donationRepository.findByRazorpayOrderId(orderId);

        if (donation == null) {
            return; // Not a donation
        }

        if ("SUCCESS".equals(donation.getStatus())) {
            return; // Idempotent safety
        }

        donation.setStatus("SUCCESS");
    }

    @Override
    public long getTotalDonors() {
        return donationRepository.getTotalDonors();
    }

    @Override
    public List<MonthlyDonationResponse> getMonthlyDonations() {
        List<Object[]> raw = donationRepository.getMonthlyDonations();
        List<MonthlyDonationResponse> result = new ArrayList<>();

        for (Object[] row : raw) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            double amount = ((Number) row[2]).doubleValue();

            result.add(new MonthlyDonationResponse(month, year, amount));
        }
        return result;
    }


    @Override
    public DonationStatsResponse getDonationStats() {
        DonationStatsResponse res = new DonationStatsResponse();
        res.setTotalAmount(donationRepository.getTotalDonatedAmount());
        res.setTotalDonors(donationRepository.getTotalDonors());
        res.setMonthlyDonations(getMonthlyDonations());
        return res;
    }




}
