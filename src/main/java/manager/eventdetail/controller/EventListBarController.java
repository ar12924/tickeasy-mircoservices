package manager.eventdetail.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import manager.eventdetail.service.DistTicketListService;
import manager.eventdetail.service.EventListBarService;
import manager.eventdetail.vo.DistTicket;
import manager.eventdetail.vo.EventInfoBarVer;
import user.notify.vo.Notification;

@Controller
@RequestMapping("eventdetail")
public class EventListBarController {

	@Autowired
	private EventListBarService eventListBarService;
	
	@PostMapping("event-list-bar")
	@ResponseBody
	public List<EventInfoBarVer> eventListBar() {

    	
	


		
		List<EventInfoBarVer> eventListBar = eventListBarService.eventListBar();
		
		
		return eventListBar;
		
		
	}
}
