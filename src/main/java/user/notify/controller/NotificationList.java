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



@WebServlet("/notificationList")
public class NotificationList extends HttpServlet{

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
	    	String memId=info.get("memberId").getAsString();
			/* String category=info.get("category").getAsString(); */
	        	
			/*Notification notificationMem =gson.fromJson(req.getReader(),Notification.class);
			
			notificationMem = notificationService.list(Integer.parseInt(memId));
			JsonObject respBody =new JsonObject();
			respBody.addProperty("success", notification!=null );		
			if(notification !=null) {
				
				
				session.setAttribute("member", member);
				respBody.addProperty("nickname", member.getNickname());
				respBody.addProperty("roleid", member.getRoleId());
			}
			resp.setContentType("application/json");
			resp.getWriter().write(respBody.toString());
			*/


			
			List<Notification> notifications = notificationService.notificationList(Integer.parseInt(memId));
			String json = gson.toJson(notifications);
			/* JsonObject respBody =new JsonObject(); */
			/*
			 * respBody.addProperty("id", members.getId());
			 * respBody.addProperty("username",members.getUsername());
			 * respBody.addProperty("nickname", members.getNickname());
			 * respBody.addProperty("pass",members.getPass());
			 * respBody.addProperty("roleId", members.getRoleId());
			 * respBody.addProperty("creator",members.getCreator());
			 * respBody.addProperty("createdDate", sdf.format(members.getCreatedDate()));
			 * respBody.addProperty("updater",members.getUpdater());
			 * respBody.addProperty("lastUpdatedDate",
			 * sdf.format(members.getLastUpdatedDate()));
			 * respBody.addProperty("username",members.getUsername());
			 */
			resp.setContentType("application/json");
			resp.getWriter().write(json);

		}

	}


