package user.notify.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import user.member.vo.Member;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;

@Controller
@RequestMapping("notify")
public class NotificationReadController {
	@Autowired
	private NotificationService notificationService;

	@PostMapping("notification-read")
	@ResponseBody
	public Map<String, Object> notificationRead(@RequestBody Notification notification,HttpSession session) {
		Map<String, Object> respBody = new HashMap<>();
		Member member = (Member) session.getAttribute("member");
		Integer memId = member.getMemberId();
		Integer memNtfId = notification.getMemberNotificationId();
		Integer notificationReadUpdate = notificationService.notificationRead(memId, memNtfId);
		if (notificationReadUpdate != null) {
			respBody.put("success", true);
		} else {
			respBody.put("success", false);
			respBody.put("message", "更新有錯");
		}
		return respBody;
	}
}
