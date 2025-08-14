package microservices.member.service;

public interface MailService {
    void sendActivationNotification(String toEmail, String userName, String tokenName);

    void sendPasswordResetNotification(String toEmail, String userName, String tokenName);

    void sendPasswordUpdateNotification(String toEmail, String userName, String tokenName);
}


