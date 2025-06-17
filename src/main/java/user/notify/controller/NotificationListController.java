package user.notify.controller;

import java.util.List;

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

public class NotificationListController{
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("notification-list")
	@ResponseBody
	public List<Notification> notificationList(@RequestBody Member member) {
		
		
    	Integer memId=member.getMemberId();
	


		
		List<Notification> notifications = notificationService.notificationList(memId);
		
		
		return notifications;
		
	}
}

/*
@WebServlet("/notification-list")

public class NotificationListController extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private NotificationService notificationService;
		public void init() throws ServletException {
				// notificationService = new NotificationServiceImpl(); //
				notificationService =CommonUtil.getBean(getServletContext(),NotificationService.class);
			
		}
		
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Gson gson =new Gson();
			Member info= gson.fromJson(req.getReader(),Member.class);
	    	Integer memId=info.getMemberId();
		


			
			List<Notification> notifications = notificationService.notificationList(memId);
			String json = gson.toJson(notifications);
			
			resp.setContentType("application/json");
			resp.getWriter().write(json);

		}

	}*/


