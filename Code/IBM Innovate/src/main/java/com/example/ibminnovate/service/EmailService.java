package com.example.ibminnovate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Temporary storage for OTPs (in-memory)
    private final Map<String, String> otpStorage = new HashMap<>();

    /**
     * Generate a random 6-digit OTP.
     */
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    /**
     * Send an OTP to the specified email.
     */
    public void sendOtpEmail(String toEmail) {
        String otp = generateOtp();
        otpStorage.put(toEmail, otp); // Store the OTP temporarily

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
    }

    /**
     * Validate the OTP entered by the user.
     */
    public boolean validateOTP(String email, String otp) {
        String storedOTP = otpStorage.get(email);
        return otp.equals(storedOTP); // Compare the provided OTP with the stored OTP
    }
    public void sendTestEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Test Email");
        message.setText("This is a test email from Spring Boot.");

        mailSender.send(message);
        System.out.println("Test email sent to: " + toEmail);
    }
}
