package user.notify.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import user.member.vo.Member;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;
import user.notify.websocket.NotifyWebSocketHandler;



@Controller
@RequestMapping("notify")

public class NotificationListController{
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
    private NotifyWebSocketHandler notifyWebSocketHandler;
	
	@GetMapping("check-login")
    @ResponseBody
    public boolean checkLoginStatus(@SessionAttribute(required = false) Member member) {
        return member != null && member.getMemberId() != null;
    }
	
	
	@PostMapping("notification-list")
	@ResponseBody
	public List<Notification> notificationList(
			/*@RequestBody Member member*/ @SessionAttribute  (required = false) Member member) {
		
    	if (member == null || member.getMemberId() == null) {
            System.out.println("未登入");
            return new ArrayList<>();
            }
    	Integer memId=member.getMemberId();
		List<Notification> notifications = notificationService.notificationList(memId);
		
		return notifications;
		
		
		
		
		/*
		 HttpSession session = req.getSession(false);
	        Member member = (session != null) ? (Member) session.getAttribute("member") : null;

	        if (member == null) {
	            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            return;
	        }

	        int memberId = member.getMemberId(); // ❗使用 session 中的 memberId，非前端傳來的
	        */
		
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


