# 🌟 DivineSpark — Live Session Booking & Payments Platform

DivineSpark is a **production-ready, client-facing web platform** for managing and booking live spiritual and wellness sessions.  
The system is designed with real-world requirements such as **secure authentication, role-based access, installment payments, admin control, and operational automation**.

---

## 🚀 Key Features

### 🔐 Authentication & Security
- Email-based authentication with JWT
- Role-based access control (USER / ADMIN)
- Secured APIs using Spring Security
- Controlled access to session join links based on payment eligibility

---

### 📅 Session Management
- Admin can create, update, and delete sessions
- Supports both **Free** and **Paid** sessions
- Seat availability management
- Users can view and book upcoming sessions

---

### 💳 Payments & Installments
- Integrated Razorpay for secure online payments
- Supports:
  - One-time full payments
  - Installment-based payments
- Tracks:
  - Paid amount
  - Remaining amount
  - Installment status per booking
- Booking lifecycle states:
  - `PENDING`
  - `PARTIALLY_PAID`
  - `CONFIRMED`
  - `CANCELLED`

---

### 🔗 Controlled Session Access
- Session join (WhatsApp) links are exposed only when eligibility is verified
- Access allowed only for `CONFIRMED` or `PARTIALLY_PAID` users
- Prevents unauthorized sharing or misuse of session links

---

### 🛠 Admin Features
- View users registered for each session
- Monitor installment payments per user
- Approve or reject user reviews
- Manage blogs, reviews, and events
- Export user, booking, and payment data as CSV for reporting

---

### ⚙️ Automation & Maintenance
- Designed to support scheduled CRON jobs
- Periodic database cleanup and archival (e.g., every 3 months)
- Helps reduce storage cost and maintain long-term performance

---

## 🧱 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- MySQL
- Razorpay Payment Gateway

### Frontend
- React
- TypeScript
- Axios
- Mobile-friendly and responsive UI

### Tools & Concepts
- REST APIs
- Webhooks for payment events
- Cron jobs for automation
- CSV export for admin reporting

---

## 🏗 Architecture Overview

- Stateless backend with JWT-based authentication
- Clear separation of layers:
  - Controllers
  - Services
  - Repositories
- Payment handling via:
  - Frontend verification (primary)
  - Webhook-based confirmation (fallback)
- Booking records preserved as immutable financial history
- Business rules enforced at the service layer

---

## 🔄 Booking & Payment Flow

1. User browses available sessions
2. Selects free or paid session
3. For paid sessions:
   - Chooses full payment or installment option
4. Payment processed via Razorpay
5. Booking status updated based on payment progress
6. Session access granted only to eligible users

---

## 🧪 Testing & Validation

- Manual testing of:
  - Free session booking
  - Full payment flow
  - Installment payments
  - Cancelled booking re-booking
  - Admin workflows
- Payment edge cases handled:
  - Webhook retries
  - Partial payments
  - Duplicate payment prevention

---

## 📦 Project Status

- Core features implemented
- Backend is production-ready
- Continuous enhancements in progress

---

## 📌 Planned Enhancements
- Automated installment reminder notifications
- Admin payment reconciliation dashboard
- Advanced analytics and reporting
- Full CRON-based archival pipeline
- Cloud deployment with monitoring

---

## 👩‍💻 Project Context

DivineSpark is a **client-facing production project**, built with a strong focus on real-world payment flows, secure access control, operational automation, and long-term maintainability.

---

## 📄 License

This project is proprietary and developed for a client use case.  
Reuse or redistribution requires prior permission.
