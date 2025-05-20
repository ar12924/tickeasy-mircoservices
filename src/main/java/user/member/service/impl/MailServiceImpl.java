package user.member.service.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import user.member.service.MailService;

public class MailServiceImpl implements MailService{
	
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;
    private static final String SMTP_USER = "xxxx@gmail.com";      // 寄件帳號
    private static final String SMTP_PASS = "P@ssWord";   
    private static final String FROM_EMAIL = "noreply@tickeasy.com";
    private static final String APP_NAME   = "TickEasy";
    private static final String VERIFY_URL = "http://localhost:8080/verify?token=";

    private final Session session;

    public MailServiceImpl() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
            }
        });
    }

    @Override
    public void sendActivationNotification(String toEmail, String userName, String tokenName) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL, APP_NAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject("【" + APP_NAME + "】帳號啟用驗證信", "UTF-8");

            String link = VERIFY_URL + tokenName;
            String html = "<p>親愛的 " + userName + "，您好：</p>"
                        + "<p>請點擊以下連結啟用帳號：</p>"
                        + "<p><a href=\"" + link + "\">" + link + "</a></p>"
                        + "<p>本連結有效期為 24 小時。</p>"
                        + "<br><p>--<br>" + APP_NAME + " TickEasy團隊敬上</p>";

            msg.setContent(html, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("寄送啟用郵件失敗", e);
        }
    }
	
	
//    以下是Spring寫法，因為DAO還沒有Spring化...
//	private final JavaMailSenderImpl mailSender;
//	
//	public MailServiceImpl() {
//		mailSender = new JavaMailSenderImpl();
//		mailSender.setHost("smtp.gmail.com");
//		mailSender.setPort(587);
//		mailSender.setUsername("xxxx@gmail.com"); //需要請求註冊 
//		mailSender.setPassword("password");
//        
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//	}
//	
//	@Override
//	public void sendActivationNotification(String toEmail, String userName, String token) {
//        String link = "http://localhost:8080/verify?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("帳號啟用驗證信");
//        message.setText("親愛的 " + userName + "：\n\n請點選以下連結啟用您的帳號：\n" + link
//        );
//        message.setFrom("noreply@tickeasy.com");
//        mailSender.send(message);
//	}

}
