package manager.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngEventInfo;

@RestController
@RequestMapping("manager/create-event")
public class CreateEventController {
	@Autowired
	private EventService service;


	@PostMapping
	public Core<Integer> createEvent(@RequestBody(required = false) MngEventInfo mngEventInfo) {
		Core<Integer> core = new Core<>();
		if (mngEventInfo == null) {
			core.setSuccessful(false);
			core.setMessage("未提供任何事件資訊");
			core.setCount(0L);
			core.setData(null);
			return core;
		}

		int result = service.createEvent(mngEventInfo);
		core.setSuccessful(true);
		core.setMessage("建立成功");
		core.setCount(1L);
		core.setData(result);
		return core;
	}
}
