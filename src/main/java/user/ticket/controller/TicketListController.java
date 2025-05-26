package user.ticket.controller;

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

import common.util.CommonUtil;
import user.notify.service.NotificationService;
import user.notify.service.impl.NotificationServiceImpl;

import user.ticket.service.TicketService;
import user.ticket.service.impl.TicketServiceImpl;
import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;



@WebServlet("/ticket-list")
public class TicketListController extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private TicketService ticketService;
		public void init() throws ServletException {
				try {
					ticketService = new TicketServiceImpl();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * ticketService =CommonUtil.getBean(getServletContext(),TicketService.class);
				 */
			
		}
		
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Gson gson =new Gson();
			JsonObject info= gson.fromJson(req.getReader(),JsonObject.class);
	    	String memId=info.get("memberId").getAsString();
		


			
			List<TicketView> ticketsView = ticketService.ticketList(Integer.parseInt(memId));
			
			String json = gson.toJson(ticketsView);
			
			resp.setContentType("application/json");
			resp.getWriter().write(json);

		}

	}


