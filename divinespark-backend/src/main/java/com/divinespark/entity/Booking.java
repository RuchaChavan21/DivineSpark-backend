package com.divinespark.entity;

import com.divinespark.entity.enums.BookingStatus;
import com.divinespark.entity.enums.PaymentType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;


    @Column(name = "whatsapp_group_link", columnDefinition = "TEXT")
    private String whatsappLink;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private double paidAmount;

    @Column(nullable = false)
    private double remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;


    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();



    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getWhatsappLink() {
        return whatsappLink;
    }

    public void setWhatsappLink(String whatsappLink) {
        this.whatsappLink = whatsappLink;
    }
}
