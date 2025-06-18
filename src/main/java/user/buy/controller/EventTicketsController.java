package user.buy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.buy.service.EventInfoService;
import user.buy.vo.TicketTypeVO;

/**
 * 活動票券類型控制器
 * 創建日期: 2025-05-12
 */
@RestController
@RequestMapping("/api/event/tickets")
public class EventTicketsController {
	@Autowired
    private EventInfoService eventService;
	
	@GetMapping("/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventTicketTypes(@PathVariable Integer eventId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (eventId == null || eventId <= 0) {
                response.put("status", 400);
                response.put("errorCode", "B0003");
                response.put("errorMessage", "無效的活動ID參數");
                response.put("userMessage", "活動ID格式不正確");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<TicketTypeVO> ticketTypes = eventService.getEventTicketTypes(eventId);
            
            if (ticketTypes != null && !ticketTypes.isEmpty()) {
                response.put("status", 200);
                response.put("data", ticketTypes);
            } else {
                response.put("status", 404);
                response.put("errorCode", "B0010");
                response.put("errorMessage", "找不到票券類型");
                response.put("userMessage", "該活動尚未設置票券類型");
            }
            
        } catch (Exception e) {
            response.put("status", 500);
            response.put("errorCode", "B0001");
            response.put("errorMessage", "系統內部錯誤：" + e.getMessage());
            response.put("userMessage", "系統暫時無法處理請求，請稍後再試");
        }
        
        return ResponseEntity.ok(response);
    }

}