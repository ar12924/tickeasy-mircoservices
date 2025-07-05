package user.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.order.dao.ShowOrderDao;
import user.order.service.ShowOrderService;
import user.order.vo.BuyerOrder;

@Service
public class ShowOrderServiceImpl implements ShowOrderService {

	@Autowired
	private ShowOrderDao showOrderDao;

	@Override
	public List<BuyerOrder> ShowOrders() {
		return showOrderDao.findAllOrders();
	}

}
