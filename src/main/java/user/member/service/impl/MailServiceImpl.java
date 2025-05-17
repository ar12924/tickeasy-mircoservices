package user.member.service.impl;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import user.member.service.MailService;

public class MailServiceImpl implements MailService{

	private final JavaMailSenderImpl mailSender;
	
	public MailServiceImpl() {
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("xxxx@gmail.com"); //需要請求註冊 
		mailSender.setPassword("password");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
	}
	
	@Override
	public void sendActivationNotification(String toEmail, String userName, String token) {
        String link = "http://localhost:8080/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("帳號啟用驗證信");
        message.setText("親愛的 " + userName + "：\n\n請點選以下連結啟用您的帳號：\n" + link
        );
        message.setFrom("noreply@tickeasy.com");
        mailSender.send(message);
	}

}
