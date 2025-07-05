package user.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.order.dao.ShowOrderDao;
import user.order.service.ShowOrderService;
import user.order.vo.BuyerOrderDC;

@Service
public class ShowOrderServiceImpl implements ShowOrderService {

	@Autowired
	private ShowOrderDao showOrderDao;

	@Override
	public List<BuyerOrderDC> ShowOrders() {
		return showOrderDao.findAllOrders();
	}

}
