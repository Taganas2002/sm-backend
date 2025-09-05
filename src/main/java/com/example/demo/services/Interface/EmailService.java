package com.example.demo.services.Interface;

public interface EmailService {
  void sendVerificationEmail(String to, String schoolName, String verifyUrl);
  void sendPasswordResetEmail(String to, String resetUrl);
}
