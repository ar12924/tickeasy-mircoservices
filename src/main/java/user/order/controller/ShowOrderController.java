package user.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import user.member.vo.Member;
import user.order.service.OrderService;
import user.order.vo.BuyerOrderDC;

@RestController
@RequestMapping("user/orders")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501" })
public class ShowOrderController {
	@Autowired
	private OrderService service;

	@GetMapping
	public Core<List<BuyerOrderDC>> showAllEvents(@SessionAttribute(required = false) Member member) {
		var core = new Core<List<BuyerOrderDC>>();

		if (member == null) {
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}
		return service.ShowOrders(member);
	}

}
