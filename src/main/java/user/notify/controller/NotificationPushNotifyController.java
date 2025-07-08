package user.notify.controller;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import user.member.vo.Member;
import user.notify.websocket.NotifyWebSocketHandler;

@Controller
@RequestMapping("notify")
public class NotificationPushNotifyController {

	 @Autowired
	    private NotifyWebSocketHandler notifyWebSocketHandler;

	    @PostMapping("test-push")
	    @ResponseBody
	    public ResponseEntity<String> testPush(@SessionAttribute(required = false) Member member) {
	        if (member == null || member.getMemberId() == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
	        }

	        Integer memId = member.getMemberId();

	        notifyWebSocketHandler.sendNotificationToMember(memId, "這是從 /test-push 傳送的通知！");
	        notifyWebSocketHandler.sendNotificationToMember(memId, "這是測試通知樣式的通知");
			new Timer().schedule(new TimerTask() {
		        @Override
		        public void run() {
		            notifyWebSocketHandler.sendNotificationToMember(memId, "這是第三封通知");
		            notifyWebSocketHandler.sendNotificationToMember(memId, "這是第四封通知");
		        }
		    }, 1000); 
		/*	new Timer().schedule(new TimerTask() {
		        @Override
		        public void run() {
		            notifyWebSocketHandler.sendNotificationToMember(memId, "這是第五封通知");
		            notifyWebSocketHandler.sendNotificationToMember(memId, "這是第六封通知");
		        }
		    }, 5000); */ // 設置 5000 毫秒後執行
	        return ResponseEntity.ok("推播成功！");
	    }
}
