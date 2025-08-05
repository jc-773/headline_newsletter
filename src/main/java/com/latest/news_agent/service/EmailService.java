package com.latest.news_agent.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String queryResponse) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hello@jonathanclark-personal.com");
        message.setTo(System.getenv(""));
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
                            <ul>
                    """);

            for (Map.Entry<String, List<String>> entry : topicToHeadlines.entrySet()) {
                String category = entry.getKey();
                List<String> responses = entry.getValue();

                htmlBuilder.append("<li><strong>").append(category).append("</strong><ul>");

                for (String raw : responses) {
                    // Split on double newlines for each topic block
                    String[] topics = raw.split("\\n\\n");

                    for (String topic : topics) {
                        topic = topic.trim();
                        if (topic.isEmpty() || !topic.startsWith("**"))
                            continue;

                        // Extract category headline (between ** and **) and remainder
                        int startIdx = topic.indexOf("**");
                        int endIdx = topic.indexOf("**", startIdx + 2);
                        if (startIdx == -1 || endIdx == -1)
                            continue;

                        String headline = topic.substring(startIdx + 2, endIdx).trim();
                        String remainder = topic.substring(endIdx + 2).trim();

                        // Extract source markdown link
                        String summaryText = remainder;
                        String sourceHtml = "";
                        int sourceStart = remainder.lastIndexOf("[Source](");
                        if (sourceStart != -1) {
                            int sourceEnd = remainder.indexOf(")", sourceStart);
                            if (sourceEnd != -1) {
                                String url = remainder.substring(sourceStart + 9, sourceEnd);
                                summaryText = remainder.substring(0, sourceStart).trim();
                                sourceHtml = String.format(" <a href=\"%s\" target=\"_blank\">[Source]</a>", url);
                            }
                        }

                        // Output HTML sub-bullet
                        htmlBuilder.append("<li><strong>")
                                .append(headline)
                                .append(":</strong> ")
                                .append(summaryText)
                                .append(sourceHtml)
                                .append("</li>");
                    }
                }

                htmlBuilder.append("</ul></li><br>");
            }

            htmlBuilder.append("""
                            </ul>
                            <p style="margin-top: 20px;">Stay informed,<br>The News Agent Team</p>
                          </body>
                        </html>
                    """);

            helper.setText(htmlBuilder.toString(), true);
            helper.setTo(System.getenv("MAILTRAP_TO_EMAIL"));
            helper.setSubject("Daily world news agent 📰");
            helper.setFrom("hello@jonathanclark-personal.com");

            mailSender.send(mimeMessage);
            log.info("Email with the following content was sent: {}", mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send formatted email", e);
        }
    }
}
