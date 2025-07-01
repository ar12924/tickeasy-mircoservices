package user.ticket.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.member.vo.Member;
import user.ticket.service.TicketExchangeService;
/**
 * 獲取會員持有票券控制器 
 * 創建者: archchang 
 * 創建日期: 2025-06-06
 */
@RestController
@RequestMapping("/api/my-tickets")
public class MyTicketsController {

	@Autowired
	private TicketExchangeService ticketExchangeService;
		
	/**
     * 獲取當前會員的票券列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyTickets(HttpSession session) {
        try {
            if (session == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            Member member = (Member) session.getAttribute("member");
            if (member == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            String nickname = member.getNickName();
            if (nickname == null || nickname.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無法取得會員資訊"));
            }

            List<Map<String, Object>> tickets = ticketExchangeService.getUserTicketsByNickname(nickname);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tickets);
            response.put("total", tickets.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("獲取票券時發生錯誤"));
        }
    }
	
	private Map<String, Object> buildErrorResponse(String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("message", message);
		return errorResponse;
	}
}