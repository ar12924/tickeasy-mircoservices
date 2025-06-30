package manager.eventdetail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public List<DistTicket> distTicketList(@RequestBody DistTicket distTicket) {
		
		
		/* Integer distId=distTicket.getDistId(); */
	


		
		List<DistTicket> distTicketLists = distTicketListService.distTicketListService();
		
		
		return distTicketLists;
		
	}
}
