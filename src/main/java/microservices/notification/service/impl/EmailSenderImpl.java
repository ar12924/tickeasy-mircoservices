package microservices.notification.service.impl;

import microservices.notification.controller.NotificationController.NotificationRequest;
import microservices.notification.service.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderImpl implements NotificationSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from-email:noreply@tickeasy.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:TickEasy}")
    private String fromName;

    @Override
    public void send(NotificationRequest request) {
        if (!"EMAIL".equalsIgnoreCase(request.getType())) {
            return;
        }
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), false);
            mailSender.send(mime);
        } catch (Exception ignore) { }
    }
}


