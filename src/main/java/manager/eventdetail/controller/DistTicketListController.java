package manager.eventdetail.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import manager.eventdetail.service.DistTicketListService;
import manager.eventdetail.vo.DistTicket;
import user.member.vo.Member;

@Controller
@RequestMapping("eventdetail")

public class DistTicketListController {

	@Autowired
	private DistTicketListService distTicketListService;

	@GetMapping("check-login")
	@ResponseBody
	public Core<Object> checkLoginStatus(@SessionAttribute(required = false) Member member) {
		Core<Object> core = new Core<>();
		  if (member == null ) {
		  
		 core.setSuccessful(false); 
		 core.setMessage("未登入，請先登入");
		 core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);		  
		 
		  return core; 
		  }
		  if (member.getRoleLevel() != 2 && member.getRoleLevel() != 3) {
			core.setSuccessful(false);
			core.setMessage("無權限訪問此功能");
			core.setAuthStatus(AuthStatus.PROHIBITED);
			return core;
		  }
		  core.setSuccessful(true); 
		  core.setMessage("成功登入");
		  core.setAuthStatus(AuthStatus.LOGGED_IN);
		return core;
	}

	@PostMapping("dist-ticket-list")
	@ResponseBody
	public List<DistTicket> distTicketList(@RequestBody Map<String, Object> json, HttpSession session) {
		String startStr = json.get("startTime") + " 00:00:00";
		String endStr = json.get("endTime") + " 23:59:59";
		String selectValue = json.get("selectValue").toString();
		Member member = (Member) session.getAttribute("member");
		if (member == null) {
			System.out.println("未登入");
			return new ArrayList<>();
		}
		if (member.getRoleLevel() != 2 && member.getRoleLevel() != 3) {
			System.out.println("無權限訪問此功能");
			return new ArrayList<>();
		}

		System.out.println("前端送來 startStr：" + startStr);
		Timestamp startTime = Timestamp.valueOf(startStr);
		Timestamp endTime = Timestamp.valueOf(endStr);
		Integer selectedId = Integer.parseInt(selectValue);

		List<DistTicket> distTicketLists = distTicketListService.distTicketListService(startTime, endTime, selectedId);

		return distTicketLists;

	}
}
