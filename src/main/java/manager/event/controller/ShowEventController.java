//package manager.event.controller;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import common.vo.Core;
//import manager.event.service.EventService;
//import manager.event.vo.MngEventInfo;
//
//@RestController
//@RequestMapping("manager/show-event")
//@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501" })
//public class ShowEventController {
//	@Autowired
//	private EventService eventService;
//
//	@GetMapping
//	public List<MngEventInfo> showAllEvents() {
//	    List<MngEventInfo> events = eventService.findAllEvents();
//
//	    if (events == null || events.isEmpty()) {
//	        return (List<MngEventInfo>) Collections.singletonMap("data", events);
//	    } else {
//	        return events;
//	    }
//	}
//
//	@GetMapping("/{id}")
//	public Core<MngEventInfo> showEvent(@PathVariable("id") Integer eventId) {
//	    MngEventInfo mngEventInfo = eventService.findEventById(eventId);
//	    Core<MngEventInfo> core = new Core<>();
//
//	    if (mngEventInfo == null) {
//	        core.setSuccessful(false);
//	        core.setMessage("找不到活動資料");
//	        core.setData(null);
//	        core.setCount(0L);
//	    } else {
//	        core.setSuccessful(true);
//	        core.setMessage("查詢成功");
//	        core.setData(mngEventInfo);
//	        core.setCount(1L);
//	    }
//	    return core;
//	}
//
//}





package manager.event.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngEventInfo;

@RestController
@RequestMapping("manager/show-event")
@CrossOrigin(origins = { 
	    "http://127.0.0.1:5500", 
	    "http://127.0.0.1:5501", 
	    "http://127.0.0.1:8080",
	    "http://localhost:5500", 
	    "http://localhost:5501", 
	    "http://localhost:8080"
	})
public class ShowEventController {
    
    @Autowired
    private EventService eventService;

    @GetMapping("member/{memberId}")
    public Core<List<MngEventInfo>> showAllEvents(@PathVariable("memberId") Integer memberId) {
        try {
            List<MngEventInfo> events = eventService.findAllEvents(memberId);
            Core<List<MngEventInfo>> core = new Core<>();
            
            if (events == null || events.isEmpty()) {
                core.setSuccessful(true);
                core.setMessage("目前沒有活動資料");
                core.setData(Collections.emptyList());
                core.setCount(0L);
                // core.setDataStatus(DataStatus.EMPTY);  // 如果有定義的話
            } else {
                core.setSuccessful(true);
                core.setMessage("查詢成功");
                core.setData(events);
                core.setCount((long) events.size());
                // core.setDataStatus(DataStatus.SUCCESS);  // 如果有定義的話
            }
            
            // core.setAuthStatus(AuthStatus.AUTHORIZED);  // 如果需要的話
            return core;
            
        } catch (Exception e) {
            Core<List<MngEventInfo>> errorCore = new Core<>();
            errorCore.setSuccessful(false);
            errorCore.setMessage("查詢失敗：" + e.getMessage());
            errorCore.setData(Collections.emptyList());
            errorCore.setCount(0L);
            // errorCore.setDataStatus(DataStatus.ERROR);  // 如果有定義的話
            return errorCore;
        }
    }

    @GetMapping("/{id}")
    public Core<MngEventInfo> showEvent(@PathVariable("id") Integer eventId) {
        try {
            // 輸入驗證
            if (eventId == null || eventId <= 0) {
                Core<MngEventInfo> core = new Core<>();
                core.setSuccessful(false);
                core.setMessage("無效的活動 ID");
                core.setData(null);
                core.setCount(0L);
                // core.setDataStatus(DataStatus.INVALID);  // 如果有定義的話
                return core;
            }
            
            MngEventInfo mngEventInfo = eventService.findEventById(eventId);
            Core<MngEventInfo> core = new Core<>();

            if (mngEventInfo == null) {
                core.setSuccessful(false);
                core.setMessage("找不到活動資料");
                core.setData(null);
                core.setCount(0L);
                // core.setDataStatus(DataStatus.NOT_FOUND);  // 如果有定義的話
            } else {
                core.setSuccessful(true);
                core.setMessage("查詢成功");
                core.setData(mngEventInfo);
                core.setCount(1L);
                // core.setDataStatus(DataStatus.SUCCESS);  // 如果有定義的話
            }
            
            // core.setAuthStatus(AuthStatus.AUTHORIZED);  // 如果需要的話
            return core;
            
        } catch (Exception e) {
            Core<MngEventInfo> errorCore = new Core<>();
            errorCore.setSuccessful(false);
            errorCore.setMessage("查詢失敗：" + e.getMessage());
            errorCore.setData(null);
            errorCore.setCount(0L);
            // errorCore.setDataStatus(DataStatus.ERROR);  // 如果有定義的話
            return errorCore;
        }
    }
}