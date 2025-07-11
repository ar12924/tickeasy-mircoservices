package manager.eventdetail.service;

import java.util.List;

import manager.eventdetail.vo.OrderDetail;
import manager.eventdetail.vo.OrderSummary;

public interface EventOrderListService {
    
    /**
     * 根據活動ID查詢訂單列表
     */
    public List<OrderSummary> findOrdersByEventId(Integer eventId);
    
    /**
     * 根據訂單ID查詢訂單明細
     */
    public OrderDetail findOrderDetailById(Integer orderId);
}