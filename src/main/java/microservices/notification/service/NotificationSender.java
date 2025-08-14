package microservices.notification.service;

import microservices.notification.controller.NotificationController.NotificationRequest;

public interface NotificationSender {
    void send(NotificationRequest request);
}


