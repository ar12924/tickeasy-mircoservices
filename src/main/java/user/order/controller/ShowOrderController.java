package user.order.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import user.order.service.ShowOrderService;
import user.order.vo.BuyerOrderDC;

@RestController
@RequestMapping("/user/orders")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://127.0.0.1:5501"})
public class ShowOrderController {
	@Autowired
	private ShowOrderService showOrderService;
	
	@GetMapping
	public <T> Core<T> showAllEvents() {
		List<BuyerOrderDC> orders = showOrderService.ShowOrders();

		if (orders == null || orders.isEmpty()) {
			Core<T> core = new Core<T>();
			core.setSuccessful(false);
			core.setMessage("目前沒有任何訂單");
			return core;
		}
		else {
			Map<String, Object> resp = new HashMap<>();
	        resp.put("data", orders); 
	        return (Core<T>) resp;
		}
	}
	
}
