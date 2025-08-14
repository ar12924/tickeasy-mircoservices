package microservices.notification.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import microservices.notification.service.NotificationSender;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationSender sender;

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody NotificationRequest req) {
        sender.send(req);
        return ResponseEntity.accepted().build();
    }

    @Data
    public static class NotificationRequest {
        private String type; // EMAIL / SMS / INAPP
        private String to;
        private String subject;
        private String body;
        private String template;
        private String token;
        private String userName;
    }
}


