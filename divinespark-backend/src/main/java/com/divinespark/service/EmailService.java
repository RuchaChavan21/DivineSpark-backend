package com.divinespark.service;

import com.divinespark.entity.enums.OtpPurpose;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String email, String otp, OtpPurpose purpose) {

        try {
            log.info("Sending OTP email to {}", email);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom("your-email@gmail.com"); // IMPORTANT
            helper.setTo(email);
            helper.setSubject("DivineSpark - OTP Verification");

            String body = """
                    <h2>DivineSpark OTP Verification</h2>
                    <p>Your OTP for <b>%s</b> is:</p>
                    <h1>%s</h1>
                    <p>This OTP is valid for 5 minutes.</p>
                    <br/>
                    <p>Regards,<br/>DivineSpark Team</p>
                    """.formatted(purpose, otp);

            helper.setText(body, true);

            mailSender.send(message);

            log.info("OTP email sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", email, e);
        }
    }

    @Async
    public void sendSessionJoinLink(
            String email,
            String sessionTitle,
            String zoomLink,
            String trainerName,
            String startTime,
            String endTime,
            String sessionType // FREE / PAID
    ) {

        try {
            log.info("Sending session join email to {}", email);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom("your-email@gmail.com");
            helper.setTo(email);
            helper.setSubject("DivineSpark - Session Access");

            String body = """
            <h2>🎉 You have successfully joined a %s session!</h2>
            <p><b>Session:</b> %s</p>
            <p><b>Trainer:</b> %s</p>
            <p><b>Start Time:</b> %s</p>
            <p><b>End Time:</b> %s</p>
            <br/>
            <p><b>Your Unique Zoom Link:</b></p>
            <p>
                <a href="%s" target="_blank"
                   style="font-size:16px;color:#2F80ED;">
                   Join Session
                </a>
            </p>
            <br/>
            <p>This link is unique to you. Do not share it.</p>
            <br/>
            <p>Regards,<br/>DivineSpark Team</p>
            """.formatted(
                    sessionType,
                    sessionTitle,
                    trainerName,
                    startTime,
                    endTime,
                    zoomLink
            );

            helper.setText(body, true);
            mailSender.send(message);

            log.info("Session join email sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("Failed to send session join email to {}", email, e);
        }
    }
    public void sendBookingCancelledEmail(
            String toEmail,
            String sessionTitle,
            String startTime) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your booking has been cancelled");
        message.setText(
                "Your booking for the session \"" + sessionTitle + "\" scheduled at "
                        + startTime + " has been successfully cancelled.\n\n"
                        + "Regards,\nDivineSpark Team"
        );

        mailSender.send(message);
    }

}