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
import org.springframework.web.bind.annotation.PathVariable;

import user.member.vo.Member;
import user.ticket.service.TicketExchangeService;
/**
 * 獲取會員持有票券控制器 
 * 創建者: archchang 
 * 創建日期: 2025-06-06
 */
@RestController
@RequestMapping("/api")
public class MyTicketsController {

	@Autowired
	private TicketExchangeService ticketExchangeService;
	
	/**
     * 檢查登入狀態
     */
    @GetMapping("/auth/status")
    public ResponseEntity<Map<String, Object>> checkAuthStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer memberId = getMemberIdFromSession(session);
            String nickname = getMemberNicknameFromSession(session);
            
            if (memberId != null && nickname != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("isLoggedIn", true);
                userData.put("nickname", nickname);
                userData.put("memberId", memberId);

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

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤");
            return ResponseEntity.internalServerError().body(response);
        }
    }
		
	/**
     * 獲取當前會員的票券列表
     */
    @GetMapping("/my-tickets")
    public ResponseEntity<Map<String, Object>> getMyTickets(HttpSession session) {
    	try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            List<Map<String, Object>> tickets = ticketExchangeService.getUserTickets(memberId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tickets);
            response.put("total", tickets.size());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("獲取票券時發生錯誤"));
        }
    }
    
    /**
     * 獲取用戶在特定活動的票券
     */
    @GetMapping("/my-tickets/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getMyTicketsByEvent(
            @PathVariable Integer eventId, 
            HttpSession session) {
        try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            if (eventId == null || eventId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的活動ID"));
            }

            List<Map<String, Object>> tickets = ticketExchangeService.getUserTicketsByEvent(memberId, eventId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tickets);
            response.put("total", tickets.size());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("獲取票券時發生錯誤"));
        }
    }
	
    /**
     * 從Session獲取會員ID
     */
    private Integer getMemberIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        Object memberObj = session.getAttribute("member");
        if (memberObj instanceof user.member.vo.Member) {
            user.member.vo.Member member = (user.member.vo.Member) memberObj;
            return member.getMemberId();
        }
        
        return null;
    }
    
    /**
     * 從Session獲取會員暱稱
     */
    private String getMemberNicknameFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        Object memberObj = session.getAttribute("member");
        if (memberObj instanceof user.member.vo.Member) {
            user.member.vo.Member member = (user.member.vo.Member) memberObj;
            return member.getNickName();
        }
        
        return null;
    }
    
	private Map<String, Object> buildErrorResponse(String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("message", message);
		return errorResponse;
	}
}