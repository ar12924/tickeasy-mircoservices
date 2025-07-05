package user.order.service;

import java.util.List;

import user.order.vo.BuyerOrder;

public interface ShowOrderService {
//	顯示所有訂單紀錄
	List<BuyerOrder> ShowOrders();
	
}