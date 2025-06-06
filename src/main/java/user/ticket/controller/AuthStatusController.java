package user.ticket.controller;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登入狀態檢查控制器 
 * 創建者: archchang 
 * 創建日期: 2025-06-05
 */
@WebServlet("/api/auth/status")
public class AuthStatusController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Gson gson;

	@Override
	public void init() throws ServletException {
		super.init();
		gson = new Gson();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		PrintWriter out = resp.getWriter();

		try {
			HttpSession session = req.getSession(false);
			Map<String, Object> response = new HashMap<>();

			if (session != null) {
				String nickname = (String) session.getAttribute("loggedInNickname");

				if (nickname != null && !nickname.trim().isEmpty()) {
					Map<String, Object> userData = new HashMap<>();
					userData.put("isLoggedIn", true);
					userData.put("nickname", nickname);

					response.put("success", true);
					response.put("data", userData);
					response.put("message", "已登入");
				} else {
					Map<String, Object> userData = new HashMap<>();
					userData.put("isLoggedIn", false);

					response.put("success", true);
					response.put("data", userData);
					response.put("message", "未登入");
				}
			} else {
				Map<String, Object> userData = new HashMap<>();
				userData.put("isLoggedIn", false);

				response.put("success", true);
				response.put("data", userData);
				response.put("message", "未登入");
			}

			out.println(gson.toJson(response));

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "系統錯誤");
			out.println(gson.toJson(errorResponse));
		}
	}
}
