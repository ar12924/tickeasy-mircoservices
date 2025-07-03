package manager.eventdetail.controller;

import manager.eventdetail.service.ManagerTicketExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 票券交換控制器
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
@RestController
@RequestMapping("/api/manager/ticket-exchange")
public class ManagerTicketExchangeController {
    
    @Autowired
    @Qualifier("managerTicketExchangeServiceImpl")
    private ManagerTicketExchangeService ticketExchangeService;
    
    /**
     * 查詢換票列表
     * 
     * @param eventId 活動ID
     * @param keyword 搜尋關鍵字
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param swappedStatus 換票狀態
     * @param page 頁數
     * @param size 每頁數量
     * @return 換票列表
     */
    @GetMapping("/swaps")
    public ResponseEntity<Map<String, Object>> getSwapExchangeList(
            @RequestParam(required = false) Integer eventId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer swappedStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Map<String, Object> result = ticketExchangeService.getSwapExchangeList(
                eventId, keyword, startDate, endDate, swappedStatus, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查詢成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorCode", "A0001");
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查詢換票列表失敗");
            response.put("errorCode", "B0001");
            response.put("errorMessage", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 獲取活動列表
     * 
     * @return 活動列表
     */
    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> getEventList() {
        try {
            List<Map<String, Object>> events = ticketExchangeService.getEventList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查詢成功");
            response.put("data", events);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查詢活動列表失敗");
            response.put("errorCode", "B0001");
            response.put("errorMessage", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 獲取換票狀態選項
     * 
     * @return 狀態列表
     */
    @GetMapping("/swap-status")
    public ResponseEntity<Map<String, Object>> getSwapStatusList() {
        try {
            List<Map<String, Object>> statusList = ticketExchangeService.getSwapStatusList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查詢成功");
            response.put("data", statusList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查詢狀態列表失敗");
            response.put("errorCode", "B0001");
            response.put("errorMessage", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}