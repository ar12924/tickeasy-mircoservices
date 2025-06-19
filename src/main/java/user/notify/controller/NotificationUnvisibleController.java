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

public class NotificationUnvisibleController{
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("notification-unvisible")
	@ResponseBody
	public Map<String, Object> notificationUnvisible(@RequestBody Notification notification) {
		
		Map<String, Object> respBody = new HashMap<>();
    	Integer memNtfId=notification.getMemberNotificationId();
	
  	
		

		
		Integer notificationVisibleUpdate = notificationService.notificationVisibleUpdate(memNtfId);
		
		
		if(notificationVisibleUpdate!=null) {
			respBody.put("success", true);
		}else {
			respBody.put("success", false);
			respBody.put("message", "更新有錯");
		}
			
		
		return respBody;
		
	}
}








/*


@WebServlet("/notification-unvisible")
public class NotificationUnvisibleController extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private NotificationService notificationService;
		public void init() throws ServletException {
			notificationService =CommonUtil.getBean(getServletContext(),NotificationService.class);
		}
		
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Gson gson =new Gson();
			JsonObject info= gson.fromJson(req.getReader(),JsonObject.class);
	    	String memberNotificationId=info.get("memberNotificationId").getAsString();
	    	
			


	

			
			Integer notificationVisibleUpdate = notificationService.notificationVisibleUpdate(Integer.parseInt(memberNotificationId));
			JsonObject respBody =new JsonObject();
				
			if(notificationVisibleUpdate!=null) {
				respBody.addProperty("success", true);
			}else {
				respBody.addProperty("success", false);
				respBody.addProperty("message", "更新有錯");
			}
			
			resp.setContentType("application/json");
			resp.getWriter().write(respBody.toString());

		}

	}
*/

