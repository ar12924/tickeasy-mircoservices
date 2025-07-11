package manager.eventdetail.dao;

import java.util.List;
import common.dao.CommonDao;
import manager.eventdetail.vo.OrderDetail;
import manager.eventdetail.vo.OrderSummary;

public interface EventOrderListDao extends CommonDao {

	/**
	 * 根據活動ID查詢訂單列表
	 */
	public List<OrderSummary> findOrdersByEventId(Integer eventId);

	/**
	 * 根據訂單ID查詢訂單明細
	 */
	public OrderDetail findOrderDetailById(Integer orderId);
}