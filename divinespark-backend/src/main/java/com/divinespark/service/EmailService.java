package com.divinespark.service;


import com.divinespark.entity.enums.OtpPurpose;
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

    @Async
    public void sendFreeSessionLink(
            String email,
            String sessionTitle,
            String zoomLink,
            String trainerName,
            String startTime,
            String endTime
    ) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("DivineSpark - Free Session Access");

            String body = """
                <h2>🎉 You have successfully joined a FREE session!</h2>
                <p><b>Session:</b> %s</p>
                <p><b>Trainer:</b> %s</p>
                <p><b>Start Time:</b> %s</p>
                <p><b>End Time:</b> %s</p>
                <br/>
                <p><b>Zoom Link:</b></p>
                <p>
                    <a href="%s" target="_blank"
                       style="font-size:16px;color:#2F80ED;">
                       Join Session
                    </a>
                </p>
                <br/>
                <p>Please join the session 5 minutes early.</p>
                <br/>
                <p>Regards,<br/>DivineSpark Team</p>
                """.formatted(
                    sessionTitle,
                    trainerName,
                    startTime,
                    endTime,
                    zoomLink
            );

            helper.setText(body, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            // Production: log error (do not fail booking)
            e.printStackTrace();
        }
    }

}