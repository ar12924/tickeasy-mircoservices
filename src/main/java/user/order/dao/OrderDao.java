package user.order.dao;

import java.util.List;

import common.dao.CommonDao;
import user.order.vo.BuyerOrderDC;

public interface OrderDao extends CommonDao {

	public List<BuyerOrderDC> findAllOrders(Integer memberId);

}
