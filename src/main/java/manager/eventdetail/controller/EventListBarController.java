package manager.eventdetail.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import manager.eventdetail.service.DistTicketListService;
import manager.eventdetail.service.EventListBarService;
import manager.eventdetail.vo.DistTicket;
import manager.eventdetail.vo.EventInfoBarVer;
import user.member.vo.Member;
import user.notify.vo.Notification;

@Controller
@RequestMapping("eventdetail")
public class EventListBarController {

	@Autowired
	private EventListBarService eventListBarService;
	
	@PostMapping("event-list-bar")
	@ResponseBody
	public List<EventInfoBarVer> eventListBar(@SessionAttribute  (required = false) Member member) {

    	
	
		Integer memId=member.getMemberId();

		Integer roleLevel=member.getRoleLevel();
		List<EventInfoBarVer> eventListBar=new ArrayList<>();
		if(roleLevel==3) {
			eventListBar = eventListBarService.eventListBar();
		}else if(roleLevel==2) {
			eventListBar = eventListBarService.eventListBarMember(memId);
		}
		
		
		
		return eventListBar;
		
		
	}
}
