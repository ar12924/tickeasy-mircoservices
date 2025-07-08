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
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501" })
public class ShowEventController {
	@Autowired
	private EventService eventService;

	@GetMapping
	public List<MngEventInfo> showAllEvents() {
	    List<MngEventInfo> events = eventService.findAllEvents();

	    if (events == null || events.isEmpty()) {
	        return (List<MngEventInfo>) Collections.singletonMap("data", events);
	    } else {
	        return events;
	    }
	}

	@GetMapping("/{id}")
	public Core<MngEventInfo> showEvent(@PathVariable("id") Integer eventId) {
	    MngEventInfo mngEventInfo = eventService.findEventById(eventId);
	    Core<MngEventInfo> core = new Core<>();

	    if (mngEventInfo == null) {
	        core.setSuccessful(false);
	        core.setMessage("找不到活動資料");
	        core.setData(null);
	        core.setCount(0L);
	    } else {
	        core.setSuccessful(true);
	        core.setMessage("查詢成功");
	        core.setData(mngEventInfo);
	        core.setCount(1L);
	    }
	    return core;
	}

}