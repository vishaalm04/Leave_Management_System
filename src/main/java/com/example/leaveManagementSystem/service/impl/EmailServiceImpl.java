package com.example.leaveManagementSystem.service.impl;

import com.example.leaveManagementSystem.entity.UserEntity;
import com.example.leaveManagementSystem.repository.UserRepository;
import com.example.leaveManagementSystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderMail;

    @Override
    public void sendLeaveAppliedEmail(UserEntity userId, String startDate, String endDate) {

        // Construct email content
        String subject = "Leave Application Submitted";
        String message = "Dear " + userId.getName() + ",\n\n" +
                "Your leave application from " + startDate + " to " + endDate + " has been submitted and is currently pending approval.\n\n" +
                "Regards,\nLeave Management System";

        // Send Email
        sendEmail(userId.getEmailId(), subject, message);
    }

    // Helper method to send email
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(senderMail);
        javaMailSender.send(message);
    }
}
