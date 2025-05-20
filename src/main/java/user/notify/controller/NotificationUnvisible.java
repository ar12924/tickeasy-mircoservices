package user.notify.controller;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import user.notify.service.NotificationService;
import user.notify.service.impl.NotificationServiceImpl;
import user.notify.vo.Notification;




@WebServlet("/notification-unvisible")
public class NotificationUnvisible extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private NotificationService notificationService;
		public void init() throws ServletException {
			try {
				notificationService = new NotificationServiceImpl();
			} catch (NamingException e) {
				e.printStackTrace();
			}
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


