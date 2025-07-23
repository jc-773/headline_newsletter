package com.latest.news_agent.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${mailtrap.to.email}")
    private String toEmail;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String queryResponse) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hello@demomailtrap.co");
        message.setTo(toEmail);
        message.setSubject("Daily world news agent 📰");
        message.setText(queryResponse);
        mailSender.send(message);
        log.info("email sent!");
    }
}
