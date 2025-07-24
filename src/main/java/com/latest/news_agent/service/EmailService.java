package com.latest.news_agent.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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

    public void sendFormattedEmail(Map<String, List<String>> topicToHeadlines) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("""
                        <html>
                          <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <h2 style="color: #2e6c80;">📰 Today's Top News</h2>
                    """);

            for (Map.Entry<String, List<String>> entry : topicToHeadlines.entrySet()) {
                htmlBuilder.append("<h3 style=\"color: #333;\">")
                        .append(entry.getKey())
                        .append("</h3><ul>");
                       String trimmedResponse = entry.getValue().toString().replaceAll("[\\[\\]]", "");
                        log.info("trimmedResponse: {}", trimmedResponse);
                    htmlBuilder.append("<li>").append(trimmedResponse).append("</li>");

                htmlBuilder.append("</ul>");
            }

            htmlBuilder.append("""
                            <p style="margin-top: 20px;">Stay informed,<br>The News Agent Team</p>
                          </body>
                        </html>
                    """);

            helper.setText(htmlBuilder.toString(), true); // true = HTML
            helper.setTo(toEmail);
            helper.setSubject("Daily world news agent 📰");
            helper.setFrom("hello@demomailtrap.co");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send formatted email", e);
        }
    }

}
