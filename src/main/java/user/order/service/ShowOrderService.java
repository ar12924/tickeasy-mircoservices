package user.order.service;

import java.util.List;

import user.order.vo.BuyerOrderDC;

public interface ShowOrderService {
//	顯示所有訂單紀錄
	List<BuyerOrderDC> ShowOrders();
	
}