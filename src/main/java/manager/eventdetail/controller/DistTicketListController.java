package manager.eventdetail.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import manager.eventdetail.service.DistTicketListService;
import manager.eventdetail.vo.DistTicket;
import user.member.vo.Member;
import user.notify.service.NotificationService;
import user.notify.vo.Notification;

@Controller
@RequestMapping("eventdetail")

public class DistTicketListController {

	@Autowired
	private DistTicketListService distTicketListService;
	
	@PostMapping("dist-ticket-list")
	@ResponseBody
	public List<DistTicket> distTicketList(@RequestBody Map<String, Object> json) {
		String startStr = json.get("startTime") + " 00:00:00";
		String endStr = json.get("endTime") + " 23:59:59";
		String selectValue = json.get("selectValue").toString();

		System.out.println("前端送來 startStr：" + startStr);
	    Timestamp startTime = Timestamp.valueOf(startStr);
	    Timestamp endTime = Timestamp.valueOf(endStr);
	    Integer selectedId = Integer.parseInt(selectValue); 
		
		
	


		
		List<DistTicket> distTicketLists = distTicketListService.distTicketListService(startTime,endTime,selectedId);
		
		
		return distTicketLists;
		
	}
}
