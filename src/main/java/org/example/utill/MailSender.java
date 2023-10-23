package org.example.utill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class MailSender {

    @Value("${mailSender.enable}")
    private boolean enable;

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    public void send(String emailTo, String subject, String message) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
