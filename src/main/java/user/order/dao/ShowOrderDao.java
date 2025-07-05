package user.order.dao;

import java.util.List;

import common.dao.CommonDao;
import user.order.vo.BuyerOrder;

public interface ShowOrderDao extends CommonDao {

	List<BuyerOrder> findAllOrders();

}
