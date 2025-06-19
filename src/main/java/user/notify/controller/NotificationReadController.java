package user.notify.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import user.notify.service.NotificationService;
import user.notify.vo.Notification;







@Controller
@RequestMapping("notify")

public class NotificationReadController{
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("notification-read")
	@ResponseBody
	public Map<String, Object> notificationRead(@RequestBody Notification notification) {
		
		Map<String, Object> respBody = new HashMap<>();
    	Integer memId=notification.getMemberId();
    	Integer memNtfId=notification.getMemberNotificationId();
	
    
			
		

		
		Integer notificationReadUpdate = notificationService.notificationRead(memId,memNtfId);
		
		
		if(notificationReadUpdate!=null) {
			respBody.put("success", true);
		}else {
			respBody.put("success", false);
			respBody.put("message", "更新有錯");
		}
		
	
		
		return respBody;
		
	}
}




/*
@WebServlet("/notification-read")
public class NotificationReadController extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private NotificationService notificationService;
		public void init() throws ServletException {
			notificationService =CommonUtil.getBean(getServletContext(),NotificationService.class);
		}
		
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Gson gson =new Gson();
			JsonObject info= gson.fromJson(req.getReader(),JsonObject.class);
	    	String memId=info.get("memberId").getAsString();
	    	String memNtfId=info.get("memberNotificationId").getAsString();
	        	
			


	

			
			Integer notificationReadUpdate = notificationService.notificationRead(Integer.parseInt(memId),Integer.parseInt(memNtfId));
			JsonObject respBody =new JsonObject();
				
			if(notificationReadUpdate!=null) {
				respBody.addProperty("success", true);
			}else {
				respBody.addProperty("success", false);
				respBody.addProperty("message", "更新有錯");
			}
			
			resp.setContentType("application/json");
			resp.getWriter().write(respBody.toString());

		}

	}*/


