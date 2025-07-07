package com.fokuswissen.user.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService
{

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    //sendet Email
    public void sendVerificationEmail(String to, String token)
    {
        String verificationUrl = "https://localhost:8080/users/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("PlanetMarsVerfication@gmx.de");
        message.setTo(to);
        message.setSubject("Bitte bestätige deine E-Mail");
        message.setText("Klicke bitte auf den Link, um deine E-Mail zu verifizieren: " + verificationUrl);
        mailSender.send(message);
    }
}
