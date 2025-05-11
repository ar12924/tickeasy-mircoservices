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




@WebServlet("/notificationUnvisible")
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


	

			
			Integer notificationVisibleUpdate = notificationService.notificationVisibleUpdate(Integer.parseInt(memberNotificationId));
			JsonObject respBody =new JsonObject();
				
			if(notificationVisibleUpdate!=null) {
				respBody.addProperty("success", true);
			}else {
				respBody.addProperty("success", false);
				respBody.addProperty("message", "更新有錯");
			}
			/*
			 * if(member != null) { String errMsg=memberService.register(member);
			 * respBody.addProperty("success", errMsg == null); if(errMsg!=null) {
			 * respBody.addProperty("errMsg", errMsg); } }else {
			 * respBody.addProperty("success",false); respBody.addProperty("errMsg",
			 * "無會員資料"); }
			 */	/* JsonObject respBody =new JsonObject(); */
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
			resp.getWriter().write(respBody.toString());

		}

	}


