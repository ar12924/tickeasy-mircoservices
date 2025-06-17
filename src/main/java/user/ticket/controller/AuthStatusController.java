package user.ticket.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.member.vo.Member;

/**
 * 登入狀態檢查控制器 
 * 創建者: archchang 
 * 創建日期: 2025-06-05
 */
@RestController
@RequestMapping("/api/auth")
public class AuthStatusController {

	/**
     * 檢查登入狀態
     */
	@GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkAuthStatus(HttpSession session) {
		
		try {
			Map<String, Object> response = new HashMap<>();

			if (session != null) {
	            Boolean loggedIn = (Boolean) session.getAttribute("loggedin");
	            Member member = (Member) session.getAttribute("member");

				if (loggedIn != null && loggedIn && member != null) {
					Map<String, Object> userData = new HashMap<>();
					userData.put("isLoggedIn", true);
					userData.put("nickname", member.getNickName());

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

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "系統錯誤");
			return ResponseEntity.internalServerError().body(errorResponse);
		}
	}
}
