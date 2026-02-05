package com.insightx.services;

import com.insightx.exceptions.EmailServiceException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Email Service - Handles all email sending operations
 * 
 * Features:
 * - Send verification emails
 * - Send password reset emails
 * - Retry logic for failed sends
 * - HTML email templates
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@insightx.com}")
    private String fromEmail;

    @Value("${app.name:InsightX}")
    private String appName;

    /**
     * Send email verification code
     */
    public void sendVerificationEmail(String toEmail, String username, String verificationCode) {
        String subject = String.format("%s - Verify Your Email", appName);
        String htmlContent = buildVerificationEmailTemplate(username, verificationCode);
        
        sendEmail(toEmail, subject, htmlContent);
        log.info("Verification email sent to: {}", toEmail);
    }

    /**
     * Send welcome email after successful verification
     */
    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = String.format("Welcome to %s!", appName);
        String htmlContent = buildWelcomeEmailTemplate(username);
        
        sendEmail(toEmail, subject, htmlContent);
        log.info("Welcome email sent to: {}", toEmail);
    }

    /**
     * Send email with retry logic
     */
    private void sendEmail(String to, String subject, String htmlContent) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                
                mailSender.send(message);
                log.debug("Email sent successfully to: {}", to);
                return;
                
            } catch (MessagingException e) {
                retryCount++;
                log.warn("Failed to send email (attempt {}/{}): {}", retryCount, maxRetries, e.getMessage());
                
                if (retryCount >= maxRetries) {
                    throw new EmailServiceException("Failed to send email after " + maxRetries + " attempts", e);
                }
                
                // Wait before retry (exponential backoff)
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new EmailServiceException("Email sending interrupted", ie);
                }
            }
        }
    }

    /**
     * Build HTML template for verification email
     */
    private String buildVerificationEmailTemplate(String username, String code) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .code { background: white; border: 2px dashed #667eea; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 8px; margin: 20px 0; border-radius: 8px; color: #667eea; }
                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>%s</h1>
                            <p>Verify Your Email Address</p>
                        </div>
                        <div class="content">
                            <h2>Hello %s!</h2>
                            <p>Thank you for registering with %s. To complete your registration and start discovering amazing content, please verify your email address.</p>
                            <p>Your verification code is:</p>
                            <div class="code">%s</div>
                            <p><strong>This code will expire in 24 hours.</strong></p>
                            <p>If you didn't create an account with us, you can safely ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 %s. All rights reserved.</p>
                            <p>This is an automated email. Please do not reply.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, appName, username, appName, code, appName);
    }

    /**
     * Build HTML template for welcome email
     */
    private String buildWelcomeEmailTemplate(String username) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to %s!</h1>
                            <p>🎉 Your account is now active</p>
                        </div>
                        <div class="content">
                            <h2>Hello %s!</h2>
                            <p>Your email has been successfully verified. You're all set to start your journey with %s!</p>
                            <p>Here's what you can do:</p>
                            <ul>
                                <li>Rate and review movies, books, and games</li>
                                <li>Get personalized recommendations</li>
                                <li>Discover new content based on your taste</li>
                                <li>Track what you've watched, read, and played</li>
                            </ul>
                            <p>Happy exploring!</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 %s. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, appName, username, appName, appName);
    }
}
