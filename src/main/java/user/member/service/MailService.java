package user.member.service;

public interface MailService {
	void sendActivationNotification(String toEmail, String userName, String token);
}
