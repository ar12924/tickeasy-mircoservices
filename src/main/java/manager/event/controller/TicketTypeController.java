package manager.event.controller;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.TicketTypeService;
import manager.event.vo.EventTicketType;

@RestController
@RequestMapping("manager")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080",
        "http://localhost:5500", "http://localhost:5501", "http://localhost:8080" })
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;

    /**
     * 建立票種
     */
    @PostMapping("/ticket-type")
    public Core<Integer> createTicketType(@RequestBody EventTicketType ticketType) {
        System.out.println("=== 建立票種 ===");
        System.out.println("接收到的票種資料: " + ticketType);
        System.out.println("票種中的 eventId: " + ticketType.getEventId());
        
        Core<Integer> core = new Core<>();
        
        try {
            if (ticketType == null) {
                System.err.println("❌ 接收到空的票種資料");
                core.setSuccessful(false);
                core.setMessage("請提供票種資料");
                return core;
            }
            
            // 驗證必要欄位
            if (ticketType.getEventId() == null || ticketType.getEventId() <= 0) {
                System.err.println("❌ 無效的活動ID: " + ticketType.getEventId());
                core.setSuccessful(false);
                core.setMessage("無效的活動ID");
                return core;
            }
            
            if (ticketType.getCategoryName() == null || ticketType.getCategoryName().trim().isEmpty()) {
                System.err.println("❌ 票種名稱為空");
                core.setSuccessful(false);
                core.setMessage("請提供票種名稱");
                return core;
            }
            
            if (ticketType.getPrice() == null || ticketType.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("❌ 無效的價格: " + ticketType.getPrice());
                core.setSuccessful(false);
                core.setMessage("請提供有效的價格");
                return core;
            }
            
            if (ticketType.getCapacity() == null || ticketType.getCapacity() <= 0) {
                System.err.println("❌ 無效的容量: " + ticketType.getCapacity());
                core.setSuccessful(false);
                core.setMessage("請提供有效的票券數量");
                return core;
            }
            
            if (ticketType.getSellFromTime() == null || ticketType.getSellToTime() == null) {
                System.err.println("❌ 販售時間不完整");
                core.setSuccessful(false);
                core.setMessage("請提供完整的販售時間");
                return core;
            }
            
            if (ticketType.getSellFromTime().after(ticketType.getSellToTime())) {
                System.err.println("❌ 販售開始時間晚於結束時間");
                core.setSuccessful(false);
                core.setMessage("販售開始時間不能晚於結束時間");
                return core;
            }
            
            System.out.println("📞 呼叫服務層建立票種...");
            Integer typeId = ticketTypeService.createTicketType(ticketType);
            System.out.println("🔄 服務層回傳的 typeId: " + typeId);
            
            if (typeId != null && typeId > 0) {
                System.out.println("✅ 票種建立成功，ID: " + typeId);
                core.setSuccessful(true);
                core.setMessage("票種建立成功");
                core.setData(typeId);
                core.setCount(1L);
            } else {
                System.err.println("❌ 票種建立失敗，回傳ID無效: " + typeId);
                core.setSuccessful(false);
                core.setMessage("票種建立失敗");
                core.setData(null);
                core.setCount(0L);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 建立票種時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
            core.setData(null);
            core.setCount(0L);
        }
        
        System.out.println("📤 票種建立最終回應: " + core);
        return core;
    }
    
    
    /**
     * 根據活動ID取得活動資訊（包含總人數上限）
     */
    @GetMapping("/event/{eventId}/info")
    public Core<EventInfo> getEventInfo(@PathVariable Integer eventId) {
        System.out.println("=== 查詢活動資訊 ===");
        System.out.println("活動ID: " + eventId);
        
        Core<EventInfo> core = new Core<>();
        
        try {
            if (eventId == null || eventId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的活動ID");
                return core;
            }
            
            EventInfo eventInfo = ticketTypeService.getEventInfo(eventId);
            
            if (eventInfo != null) {
                System.out.println("✅ 查詢到活動: " + eventInfo.getEventName());
                System.out.println("總人數上限: " + eventInfo.getTotalCapacity());
                core.setSuccessful(true);
                core.setMessage("查詢成功");
                core.setData(eventInfo);
                core.setCount(1L);
            } else {
                System.err.println("❌ 找不到活動ID: " + eventId);
                core.setSuccessful(false);
                core.setMessage("找不到指定的活動");
                core.setData(null);
                core.setCount(0L);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 查詢活動失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(null);
            core.setCount(0L);
        }
        
        return core;
    }

    // 新增內部類別來傳遞活動資訊
    public static class EventInfo {
        private String eventName;
        private Integer totalCapacity;
        
        public EventInfo() {}
        
        public EventInfo(String eventName, Integer totalCapacity) {
            this.eventName = eventName;
            this.totalCapacity = totalCapacity;
        }
        
        // Getters and Setters
        public String getEventName() { return eventName; }
        public void setEventName(String eventName) { this.eventName = eventName; }
        public Integer getTotalCapacity() { return totalCapacity; }
        public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }
    }
    
    
    /**
     * 根據活動ID取得所有票種
     */
    @GetMapping("ticket-type/event/{eventId}")
    public Core<List<EventTicketType>> getTicketTypesByEventId(@PathVariable Integer eventId) {
        System.out.println("=== 查詢活動票種 ===");
        System.out.println("活動ID: " + eventId);
        
        Core<List<EventTicketType>> core = new Core<>();
        
        try {
            if (eventId == null || eventId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的活動ID");
                return core;
            }
            
            List<EventTicketType> ticketTypes = ticketTypeService.findTicketTypesByEventId(eventId);
            
            System.out.println("✅ 查詢到 " + ticketTypes.size() + " 個票種");
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(ticketTypes);
            core.setCount((long) ticketTypes.size());
            
        } catch (Exception e) {
            System.err.println("❌ 查詢票種失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(List.of());
            core.setCount(0L);
        }
        
        return core;
    }
    
    /**
     * 根據票種ID取得單一票種
     */
    @GetMapping("/ticket-type/{typeId}")
    public Core<EventTicketType> getTicketTypeById(@PathVariable Integer typeId) {
        System.out.println("=== 查詢單一票種 ===");
        System.out.println("票種ID: " + typeId);
        
        Core<EventTicketType> core = new Core<>();
        
        try {
            if (typeId == null || typeId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的票種ID");
                return core;
            }
            
            EventTicketType ticketType = ticketTypeService.findTicketTypeById(typeId);
            
            if (ticketType != null) {
                System.out.println("✅ 查詢到票種: " + ticketType.getCategoryName());
                core.setSuccessful(true);
                core.setMessage("查詢成功");
                core.setData(ticketType);
                core.setCount(1L);
            } else {
                System.err.println("❌ 找不到票種ID: " + typeId);
                core.setSuccessful(false);
                core.setMessage("找不到指定的票種");
                core.setData(null);
                core.setCount(0L);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 查詢票種失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(null);
            core.setCount(0L);
        }
        
        return core;
    }
    
    /**
     * 更新票種
     */
    @PutMapping("/ticket-type/{typeId}")
    public Core<Integer> updateTicketType(@PathVariable Integer typeId, @RequestBody EventTicketType ticketType) {
        System.out.println("=== 更新票種 ===");
        System.out.println("票種ID: " + typeId);
        System.out.println("更新資料: " + ticketType);
        
        Core<Integer> core = new Core<>();
        
        try {
            if (typeId == null || typeId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的票種ID");
                return core;
            }
            
            if (ticketType == null) {
                core.setSuccessful(false);
                core.setMessage("請提供票種資料");
                return core;
            }
            
            // 設定票種ID
            ticketType.setTypeId(typeId);
            
            int result = ticketTypeService.updateTicketType(ticketType);
            
            if (result > 0) {
                System.out.println("✅ 票種更新成功");
                core.setSuccessful(true);
                core.setMessage("票種更新成功");
                core.setData(typeId);
                core.setCount(1L);
            } else {
                System.err.println("❌ 票種更新失敗");
                core.setSuccessful(false);
                core.setMessage("票種更新失敗");
                core.setData(null);
                core.setCount(0L);
            }
            
        } catch (Exception e) {
            System.err.println("❌ 更新票種失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
            core.setData(null);
            core.setCount(0L);
        }
        
        return core;
    }
}