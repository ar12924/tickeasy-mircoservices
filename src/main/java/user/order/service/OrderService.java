package user.order.service;

import java.util.List;

import common.vo.Core;
import user.member.vo.Member;
import user.order.vo.BuyerOrderDC;

public interface OrderService {
//	顯示所有訂單紀錄
	Core<List<BuyerOrderDC>> ShowOrders(Member member);
	
}