package microservices.notify.controller;

import microservices.notify.dao.NotificationDao;
import microservices.notify.vo.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class NotifyApiController {
    @GetMapping("/check-login")
    public boolean checkLogin() {
        // 前期先一律當作已登入，之後可接入 JWT 驗證或從 Gateway 傳遞 principal
        return true;
    }

    @Autowired
    private NotificationDao notificationDao;

    @PostMapping("/notification-list")
    public List<Notification> list(@RequestBody Map<String, Object> body) {
        Integer memberId = (Integer) body.get("memberId");
        return notificationDao.selectAllByMemberId(memberId);
    }

    @PostMapping("/notification-read")
    public Map<String, Object> read(@RequestBody Map<String, Object> body) {
        Integer memberId = (Integer) body.get("memberId");
        Integer memberNotificationId = (Integer) body.get("memberNotificationId");
        Integer updated = notificationDao.updateIsRead(memberId, memberNotificationId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", updated != null);
        return resp;
    }

    @PostMapping("/notification-unvisible")
    public Map<String, Object> unvisible(@RequestBody Map<String, Object> body) {
        Integer memberNotificationId = (Integer) body.get("memberNotificationId");
        Integer updated = notificationDao.updateUnvisible(memberNotificationId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", updated != null);
        return resp;
    }

    @PostMapping("/notification-clear-all")
    public Map<String, Object> clear(@RequestBody Map<String, Object> body) {
        Integer memberId = (Integer) body.get("memberId");
        Integer updated = notificationDao.updateListClear(memberId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", updated != null);
        return resp;
    }
}


