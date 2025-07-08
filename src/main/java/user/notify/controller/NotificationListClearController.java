package user.notify.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import user.member.vo.Member;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;

@Controller
@RequestMapping("notify")
public class NotificationListClearController {
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("notification-clear-all")
	@ResponseBody
	public Core<Object> notificationListClear(@SessionAttribute(required = false) Member member) {
		
		Integer memId=member.getMemberId();
		Map<String, Object> respBody = new HashMap<>();
		Core<Object> result = new Core<>();
    	
	
  	
		

		
		Integer notificationListClearUpdate = notificationService.notificationListClearUpdate(memId);
		
		
		if(notificationListClearUpdate!=null) {
			result.setSuccessful(true);
			result.setMessage("已將所有通知刪除");
			
			return result;
		}else {
			result.setSuccessful(false);
			result.setMessage("刪除所有通知失敗");
			return result;
		}
			
		
		
	}
}


