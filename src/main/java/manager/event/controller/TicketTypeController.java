package manager.event.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("manager/ticket-type")
@CrossOrigin(origins = { 
    "http://127.0.0.1:5500", 
    "http://127.0.0.1:5501", 
    "http://127.0.0.1:8080",
    "http://localhost:5500", 
    "http://localhost:5501", 
    "http://localhost:8080"
})
public class TicketTypeController {
    
    @Autowired
    private TicketTypeService ticketTypeService;
    
    @GetMapping("/event/{eventId}")
    public Core<List<EventTicketType>> getTicketTypesByEvent(@PathVariable Integer eventId) {
        Core<List<EventTicketType>> core = new Core<>();
        
        try {
            if (eventId == null || eventId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的活動ID");
                core.setData(List.of());
                return core;
            }
            
            List<EventTicketType> ticketTypes = ticketTypeService.findTicketTypesByEventId(eventId);
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(ticketTypes);
            core.setCount((long) ticketTypes.size());
            
        } catch (Exception e) {
            System.err.println("查詢票種失敗: " + e.getMessage());
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(List.of());
        }
        
        return core;
    }
    
    @PostMapping
    public Core<Integer> createTicketType(@RequestBody EventTicketType ticketType) {
        Core<Integer> core = new Core<>();
        
        try {
            if (ticketType == null) {
                core.setSuccessful(false);
                core.setMessage("未提供票種資料");
                return core;
            }
            
            String validationError = validateTicketType(ticketType);
            if (validationError != null) {
                core.setSuccessful(false);
                core.setMessage(validationError);
                return core;
            }
            
            int result = ticketTypeService.createTicketType(ticketType);
            
            if (result > 0) {
                core.setSuccessful(true);
                core.setMessage("票種新增成功");
                core.setData(ticketType.getTypeId());
            } else {
                core.setSuccessful(false);
                core.setMessage("票種新增失敗");
            }
            
        } catch (Exception e) {
            System.err.println("新增票種失敗: " + e.getMessage());
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
        }
        
        return core;
    }
    
    @PutMapping("/{typeId}")
    public Core<Integer> updateTicketType(@PathVariable Integer typeId, @RequestBody EventTicketType ticketType) {
        Core<Integer> core = new Core<>();
        
        try {
            if (typeId == null || typeId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的票種ID");
                return core;
            }
            
            ticketType.setTypeId(typeId);
            
            String validationError = validateTicketType(ticketType);
            if (validationError != null) {
                core.setSuccessful(false);
                core.setMessage(validationError);
                return core;
            }
            
            int result = ticketTypeService.updateTicketType(ticketType);
            
            if (result > 0) {
                core.setSuccessful(true);
                core.setMessage("票種更新成功");
                core.setData(typeId);
            } else {
                core.setSuccessful(false);
                core.setMessage("票種更新失敗");
            }
            
        } catch (Exception e) {
            System.err.println("更新票種失敗: " + e.getMessage());
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
        }
        
        return core;
    }
    
    @DeleteMapping("/{typeId}")
    public Core<Integer> deleteTicketType(@PathVariable Integer typeId) {
        Core<Integer> core = new Core<>();
        
        try {
            if (typeId == null || typeId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的票種ID");
                return core;
            }
            
            int result = ticketTypeService.deleteTicketType(typeId);
            
            if (result > 0) {
                core.setSuccessful(true);
                core.setMessage("票種刪除成功");
                core.setData(typeId);
            } else {
                core.setSuccessful(false);
                core.setMessage("票種刪除失敗");
            }
            
        } catch (Exception e) {
            System.err.println("刪除票種失敗: " + e.getMessage());
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
        }
        
        return core;
    }
    
    private String validateTicketType(EventTicketType ticketType) {
        if (ticketType.getCategoryName() == null || ticketType.getCategoryName().trim().isEmpty()) {
            return "票種名稱不可為空";
        }
        
        if (ticketType.getSellFromTime() == null || ticketType.getSellToTime() == null) {
            return "請填寫販售時間";
        }
        
        if (ticketType.getSellFromTime().compareTo(ticketType.getSellToTime()) >= 0) {
            return "販售結束時間必須大於開始時間";
        }
        
        if (ticketType.getPrice() == null || ticketType.getPrice().doubleValue() < 0) {
            return "票價不可為負數";
        }
        
        if (ticketType.getCapacity() == null || ticketType.getCapacity() <= 0) {
            return "票券數量必須大於0";
        }
        
        if (ticketType.getEventId() == null || ticketType.getEventId() <= 0) {
            return "無效的活動ID";
        }
        
        return null;
    }
}