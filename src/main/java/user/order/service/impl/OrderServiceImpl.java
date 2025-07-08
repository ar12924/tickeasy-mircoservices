package user.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.vo.Core;
import common.vo.DataStatus;
import user.member.vo.Member;
import user.order.dao.OrderDao;
import user.order.service.OrderService;
import user.order.vo.BuyerOrderDC;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Transactional
	@Override
	public Core<List<BuyerOrderDC>> ShowOrders(Member member) {
		var core = new Core<List<BuyerOrderDC>>();
		var memberId = member.getMemberId();
		List<BuyerOrderDC> buyerOrderDC = orderDao.findAllOrders(memberId);

		// 如果查不到資料，回傳空的 List
		if (buyerOrderDC.isEmpty()) {
			core.setDataStatus(DataStatus.NOT_FOUND);
			core.setMessage("沒有任何訂單");
			core.setSuccessful(false);
			return core;
		}
		// 查到資料，回傳有資料的 List
		core.setDataStatus(DataStatus.FOUND);
		core.setData(buyerOrderDC);
		core.setMessage("有訂單");
		core.setSuccessful(true);
		return core;
	}

}
