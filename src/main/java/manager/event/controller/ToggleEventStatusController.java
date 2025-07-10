package manager.event.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngEventInfo;

@RestController
@RequestMapping("manager")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080",
		"http://localhost:5500", "http://localhost:5501", "http://localhost:8080" })

public class ToggleEventStatusController {

	@Autowired
	private EventService service;
	
	@PutMapping("toggle-event-status/{eventId}")
	public Core<Integer> toggleEventStatus(@PathVariable Integer eventId, @RequestBody Map<String, Object> request) {
	    Core<Integer> core = new Core<>();
	    
	    try {
	        if (eventId == null || eventId <= 0) {
	            core.setSuccessful(false);
	            core.setMessage("無效的活動ID");
	            return core;
	        }
	        
	        // 從請求中取得新的狀態
	        Integer newStatus = (Integer) request.get("isPosted");
	        if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
	            core.setSuccessful(false);
	            core.setMessage("無效的狀態值");
	            return core;
	        }
	        
	        // 檢查活動是否存在
	        MngEventInfo existingEvent = service.findEventById(eventId);
	        if (existingEvent == null) {
	            core.setSuccessful(false);
	            core.setMessage("找不到指定的活動");
	            return core;
	        }
	        
	        // 更新狀態
	        int result = service.toggleEventStatus(eventId, newStatus);
	        
	        if (result > 0) {
	            String statusText = newStatus == 1 ? "上架" : "下架";
	            core.setSuccessful(true);
	            core.setMessage("活動" + statusText + "成功");
	            core.setData(eventId);
	        } else {
	            core.setSuccessful(false);
	            core.setMessage("狀態更新失敗");
	        }
	        
	    } catch (Exception e) {
	        System.err.println("切換活動狀態失敗: " + e.getMessage());
	        e.printStackTrace();
	        
	        core.setSuccessful(false);
	        core.setMessage("系統錯誤：" + e.getMessage());
	    }
	    
	    return core;
	}
}
