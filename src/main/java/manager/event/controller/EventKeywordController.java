package manager.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import manager.event.service.EventService;
import manager.event.vo.MngKeywordCategory;

@RestController
@RequestMapping("manager/show-event")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501" })
public class EventKeywordController {
	@Autowired
	private EventService eventService;
	
	@PostMapping
	public Core<Integer> createKeywordCategory(@RequestBody(required = false) MngKeywordCategory mngKeywordCategory) {
		return null;
		
	}
	
}
