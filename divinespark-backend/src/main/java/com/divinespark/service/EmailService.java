package com.divinespark.service;


import com.divinespark.entity.OtpPurpose;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String email, String otp, OtpPurpose purpose) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

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

        } catch (MessagingException e) {
            // In production → log this properly
            e.printStackTrace();
        }
    }
}