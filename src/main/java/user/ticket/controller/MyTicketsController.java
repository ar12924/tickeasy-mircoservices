package user.ticket.controller;

import com.google.gson.Gson;
import common.util.CommonUtil;
import user.ticket.service.TicketExchangeService;
import user.member.vo.Member;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 獲取會員持有票券控制器 
 * 創建者: archchang 
 * 創建日期: 2025-06-06
 */
@WebServlet("/api/my-tickets")
public class MyTicketsController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TicketExchangeService ticketExchangeService;
	private Gson gson;

	@Override
	public void init() throws ServletException {
		super.init();
		ticketExchangeService = CommonUtil.getBean(getServletContext(), TicketExchangeService.class);
		gson = new Gson();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		PrintWriter out = resp.getWriter();

		try {
			HttpSession session = req.getSession(false);
			if (session == null) {
				buildErrorResponse(out, "未登入或登入已過期");
				return;
			}

			Member member = (Member) session.getAttribute("member");
	        if (member == null) {
	            buildErrorResponse(out, "未登入或登入已過期");
	            return;
	        }

	        String nickname = member.getNickName();
	        if (nickname == null || nickname.trim().isEmpty()) {
	            buildErrorResponse(out, "無法取得會員資訊");
	            return;
	        }
	        
			// 透過 Service 獲取用戶票券
			List<Map<String, Object>> tickets = ticketExchangeService.getUserTicketsByNickname(nickname);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", tickets);
			response.put("total", tickets.size());

			out.println(gson.toJson(response));

		} catch (Exception e) {
			e.printStackTrace();
			buildErrorResponse(out, "獲取票券時發生錯誤");
		}
	}

	private void buildErrorResponse(PrintWriter out, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("message", message);
		out.println(gson.toJson(errorResponse));
	}
}