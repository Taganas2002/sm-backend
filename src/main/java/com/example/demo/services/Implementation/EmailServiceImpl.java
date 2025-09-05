package com.example.demo.services.Implementation;

import com.example.demo.services.Interface.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  @Value("${spring.mail.username}") private String from;

  public EmailServiceImpl(JavaMailSender mailSender) { this.mailSender = mailSender; }

  @Override
  public void sendVerificationEmail(String to, String schoolName, String verifyUrl) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject("Verify your email to start " + (schoolName != null ? schoolName : "your account"));
    msg.setText("""
        Welcome!

        Please verify your email to start using the app:
        %s

        This link will expire soon.
        """.formatted(verifyUrl));
    mailSender.send(msg);
  }

  @Override
  public void sendPasswordResetEmail(String to, String resetUrl) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject("Reset your password");
    msg.setText("""
        We received a request to reset your password.

        Click the link below to set a new password:
        %s

        If you didn't request this, ignore this email.
        """.formatted(resetUrl));
    mailSender.send(msg);
  }
}
