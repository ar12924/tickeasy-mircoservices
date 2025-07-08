package user.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import user.member.service.MailService;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

// 使用 JavaMail 實現 with OAuth 2.0
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

    @Value("${app.mail.reset.subject:重設TickEasy密碼}")
    private String resetSubject;

    @Override
    public void sendActivationNotification(String toEmail, String userName, String tokenName) {
        String subject = activationSubject;
        String content = buildActivationEmailContent(userName, tokenName);
        sendHtmlEmail(toEmail, subject, content);
    }

    @Override
    public void sendPasswordResetNotification(String toEmail, String userName, String tokenName) {
        String subject = resetSubject;
        String content = buildPasswordResetEmailContent(userName, tokenName);
        sendHtmlEmail(toEmail, subject, content);
    }

    @Override
    public void sendPasswordUpdateNotification(String toEmail, String userName, String tokenName) {
        String subject = "TickEasy - 密碼更新確認";
        String content = buildPasswordUpdateEmailContent(userName, tokenName);
        sendHtmlEmail(toEmail, subject, content);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            // 只用基本認證
            sendEmailWithBasicAuth(toEmail, subject, htmlContent);
        } catch (Exception e) {
            System.err.println("郵件發送失敗: " + e.getMessage());
            throw new RuntimeException("郵件發送失敗: " + e.getMessage(), e);
        }
    }

    private void sendEmailWithBasicAuth(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("基本認證郵件發送成功: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("基本認證郵件發送失敗 (MessagingException): " + e.getMessage());
            throw new RuntimeException("郵件發送失敗: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            System.err.println("郵件發送失敗 (UnsupportedEncodingException): " + e.getMessage());
            throw new RuntimeException("郵件編碼失敗: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("郵件發送失敗 (其他異常): " + e.getMessage());
            throw new RuntimeException("郵件發送失敗: " + e.getMessage(), e);
        }
    }


    private String buildActivationEmailContent(String userName, String tokenName) {
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <title>帳號驗證</title>" +
                        "</head>" +
                        "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                        "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                        "        <h2 style=\"color: #007bff;\">歡迎加入 TickEasy！</h2>" +
                        "        <p>親愛的 %s，</p>" +
                        "        <p>感謝您註冊 TickEasy 帳號。請點擊下方按鈕驗證您的電子郵件地址：</p>" +
                        "        <div style=\"text-align: center; margin: 30px 0;\">" +
                        "            <a href=\"http://localhost:8080/maven-tickeasy-v1/user/member/verify?token=%s\" " +
                        "               style=\"background-color: #007bff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;\">" +
                        "                驗證帳號" +
                        "            </a>" +
                        "        </div>" +
                        "        <p>如果您無法點擊按鈕，請複製以下連結到瀏覽器：</p>" +
                        "        <p style=\"word-break: break-all; color: #666;\">" +
                        "            http://localhost:8080/maven-tickeasy-v1/user/member/verify?token=%s" +
                        "        </p>" +
                        "        <p>此連結將在 24 小時後失效。</p>" +
                        "        <hr style=\"margin: 30px 0; border: none; border-top: 1px solid #eee;\">" +
                        "        <p style=\"font-size: 12px; color: #666;\">" +
                        "            此郵件由 TickEasy 系統自動發送，請勿回覆。<br>" +
                        "            如果您沒有註冊 TickEasy 帳號，請忽略此郵件。" +
                        "        </p>" +
                        "    </div>" +
                        "</body>" +
                        "</html>",
                userName, tokenName, tokenName);
    }

    private String buildPasswordResetEmailContent(String userName, String tokenName) {
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <title>密碼重設</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        "        .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }" +
                        "        .content { padding: 20px; background-color: #f8f9fa; }" +
                        "        .button { display: inline-block; background-color: #dc3545; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                        "        .footer { text-align: center; padding: 20px; color: #6c757d; font-size: 14px; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h2 style=\"color: #dc3545;\">TickEasy 密碼重設</h2>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <p>我們收到了您的密碼重設請求。請點擊下方按鈕重設您的密碼：</p>" +
                        "        <div style=\"text-align: center; margin: 30px 0;\">" +
                        "            <a href=\"http://localhost:8080/maven-tickeasy-v1/user/member/reset-password.html?token=%s\" " +
                        "               style=\"background-color: #dc3545; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;\">" +
                        "                重置密碼" +
                        "            </a>" +
                        "        </div>" +
                        "        <p>如果您無法點擊按鈕，請複製以下連結到瀏覽器：</p>" +
                        "        <p style=\"word-break: break-all; color: #666;\">" +
                        "            http://localhost:8080/maven-tickeasy-v1/user/member/reset-password.html?token=%s" +
                        "        </p>" +
                        "        <p>此連結將在 1 小時後失效。</p>" +
                        "        <p><strong>如果您沒有請求重置密碼，請忽略此郵件。</strong></p>" +
                        "        <hr style=\"margin: 30px 0; border: none; border-top: 1px solid #eee;\">" +
                        "        <p style=\"font-size: 12px; color: #666;\">" +
                        "            此郵件由 TickEasy 系統自動發送，請勿回覆。" +
                        "        </p>" +
                        "    </div>" +
                        "</body>" +
                        "</html>",
                userName, tokenName, tokenName);
    }

    private String buildPasswordUpdateEmailContent(String userName, String tokenName) {
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "    <meta charset=\"UTF-8\">" +
                        "    <title>密碼更新確認</title>" +
                        "    <style>" +
                        "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        "        .header { background-color: #ec4899; color: white; padding: 20px; text-align: center; }" +
                        "        .content { padding: 20px; background-color: #f8f9fa; }" +
                        "        .button { display: inline-block; background-color: #ec4899; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                        "        .footer { text-align: center; padding: 20px; color: #6c757d; font-size: 14px; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class=\"container\">" +
                        "        <div class=\"header\">" +
                        "            <h2 style=\"color: white;\">TickEasy 密碼更新確認</h2>" +
                        "        </div>" +
                        "        <div class=\"content\">" +
                        "            <p>親愛的 %s，</p>" +
                        "            <p>我們收到了您的密碼更新請求。請點擊下方按鈕確認您的密碼更新：</p>" +
                        "        <div style=\"text-align: center; margin: 30px 0;\">" +
                        "            <a href=\"http://localhost:8080/maven-tickeasy-v1/user/member/edit/verify-password-update?token=%s\" " +
                        "               style=\"background-color: #ec4899; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;\">" +
                        "                確認更新" +
                        "            </a>" +
                        "        </div>" +
                        "        <p>如果您無法點擊按鈕，請複製以下連結到瀏覽器：</p>" +
                        "        <p style=\"word-break: break-all; color: #666;\">" +
                        "            http://localhost:8080/maven-tickeasy-v1/user/member/edit/verify-password-update?token=%s" +
                        "        </p>" +
                        "        <p>此連結將在 1 小時後失效。</p>" +
                        "        <p><strong>如果您沒有請求密碼更新，請忽略此郵件。</strong></p>" +
                        "        <hr style=\"margin: 30px 0; border: none; border-top: 1px solid #eee;\">" +
                        "        <p style=\"font-size: 12px; color: #666;\">" +
                        "            此郵件由 TickEasy 系統自動發送，請勿回覆。" +
                        "        </p>" +
                        "    </div>" +
                        "</body>" +
                        "</html>",
                userName, tokenName, tokenName);
    }
}
