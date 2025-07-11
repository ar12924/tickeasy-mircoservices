package manager.eventdetail.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import manager.eventdetail.dao.EventOrderListDao;
import manager.eventdetail.service.EventOrderListService;
import manager.eventdetail.vo.OrderDetail;
import manager.eventdetail.vo.OrderSummary;

@Service
public class EventOrderListServiceImpl implements EventOrderListService {
    
    @Autowired
    private EventOrderListDao orderDao;
    
    @Override
    public List<OrderSummary> findOrdersByEventId(Integer eventId) {
        return orderDao.findOrdersByEventId(eventId);
    }
    
    @Override
    public OrderDetail findOrderDetailById(Integer orderId) {
        return orderDao.findOrderDetailById(orderId);
    }
}