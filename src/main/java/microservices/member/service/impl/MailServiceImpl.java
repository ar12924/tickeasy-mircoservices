package microservices.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import microservices.member.service.MailService;

import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from-email:noreply@tickeasy.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:TickEasy}")
    private String fromName;

    @Value("${app.mail.activation.subject:歡迎加入 TickEasy - 請驗證您的帳號}")
    private String activationSubject;

    @Value("${app.mail.reset.subject:TickEasy - 密碼重設通知}")
    private String resetSubject;

    @Value("${app.mail.update.subject:TickEasy - 密碼更新確認}")
    private String updateSubject;

    @Value("${app.url.member-service:http://localhost:8080/maven-tickeasy-v1}")
    private String memberServiceBaseUrl;

    @Value("${app.url.reset-password-page:http://localhost:8080/maven-tickeasy-v1/user/member/reset-password.html}")
    private String resetPasswordPageUrl;

    @Value("${app.url.password-update-page:http://localhost:8080/maven-tickeasy-v1/user/member/edit.html}")
    private String passwordUpdatePageUrl;

    @Override
    public void sendActivationNotification(String toEmail, String userName, String tokenName) {
        String url = memberServiceBaseUrl + "/user/member/verify?token=" + tokenName;
        String body = String.format("%s 您好：\n\n請點擊以下連結完成帳號驗證：\n%s\n\n如非本人操作請忽略本信。", userName, url);
        send(toEmail, activationSubject, body);
    }

    @Override
    public void sendPasswordResetNotification(String toEmail, String userName, String tokenName) {
        String url = resetPasswordPageUrl + "?token=" + tokenName;
        String body = String.format("%s 您好：\n\n請點擊以下連結重設密碼：\n%s\n\n如非本人操作請忽略本信。", userName, url);
        send(toEmail, resetSubject, body);
    }

    @Override
    public void sendPasswordUpdateNotification(String toEmail, String userName, String tokenName) {
        String url = passwordUpdatePageUrl + "?token=" + tokenName;
        String body = String.format("%s 您好：\n\n請點擊以下連結確認密碼更新：\n%s\n\n如非本人操作請忽略本信。", userName, url);
        send(toEmail, updateSubject, body);
    }

    private void send(String to, String subject, String text) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(mime);
        } catch (Exception ignore) { }
    }
}
