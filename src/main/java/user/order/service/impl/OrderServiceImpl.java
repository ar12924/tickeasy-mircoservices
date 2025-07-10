//package user.order.service.impl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import common.vo.Core;
//import common.vo.DataStatus;
//import user.member.vo.Member;
//import user.order.dao.OrderDao;
//import user.order.service.OrderService;
//import user.order.vo.BuyerOrderDC;
//
//@Service
//public class OrderServiceImpl implements OrderService {
//
//	@Autowired
//	private OrderDao orderDao;
//
//	@Transactional
//	@Override
//	public Core<List<BuyerOrderDC>> ShowOrders(Member member) {
//		var core = new Core<List<BuyerOrderDC>>();
//		var memberId = member.getMemberId();
//		List<BuyerOrderDC> buyerOrderDC = orderDao.findAllOrders(memberId);
//
//		// 如果查不到資料，回傳空的 List
//		if (buyerOrderDC.isEmpty()) {
//			core.setDataStatus(DataStatus.NOT_FOUND);
//			core.setMessage("沒有任何訂單");
//			core.setSuccessful(false);
//			return core;
//		}
//		// 查到資料，回傳有資料的 List
//		core.setDataStatus(DataStatus.FOUND);
//		core.setData(buyerOrderDC);
//		core.setMessage("有訂單");
//		core.setSuccessful(true);
//		return core;
//	}
//
//}



package user.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.order.dao.OrderDao;
import user.order.service.OrderService;
import user.order.vo.BuyerOrderDC;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderDao orderDao;
    
    @Override
    public List<BuyerOrderDC> getOrdersByMemberId(Integer memberId) {
        return orderDao.findOrdersByMemberId(memberId);
    }
    
    @Override
    public BuyerOrderDC getOrderById(Integer orderId) {
        return orderDao.findOrderById(orderId);
    }
    
    @Override
    public List<BuyerOrderDC> getOrdersByMemberIdAndStatus(Integer memberId, String orderStatus) {
        return orderDao.findOrdersByMemberIdAndStatus(memberId, orderStatus);
    }
    
    @Override
    @Transactional
    public int createOrder(BuyerOrderDC order) {
        return orderDao.createOrder(order);
    }
    
    @Override
    @Transactional
    public int updateOrder(BuyerOrderDC order) {
        return orderDao.updateOrder(order);
    }
    
    @Override
    @Transactional
    public int updateOrderStatus(Integer orderId, String orderStatus, Boolean isPaid) {
        return orderDao.updateOrderStatus(orderId, orderStatus, isPaid);
    }
    
    @Override
    @Transactional
    public int deleteOrder(Integer orderId) {
        return orderDao.deleteOrder(orderId);
    }
    
    @Override
    public List<BuyerOrderDC> getOrdersByEventId(Integer eventId) {
        return orderDao.findOrdersByEventId(eventId);
    }
    
    @Override
    public Map<String, Long> getOrderStatsByMemberId(Integer memberId) {
        Map<String, Long> stats = new HashMap<>();
        
        try {
            // 全部訂單
            Long totalCount = orderDao.countOrdersByMemberId(memberId);
            stats.put("total", totalCount);
            
            // 待付款
            Long pendingCount = orderDao.countOrdersByMemberIdAndStatus(memberId, "PENDING");
            stats.put("pending", pendingCount);
            
            // 已付款
            Long paidCount = orderDao.countOrdersByMemberIdAndStatus(memberId, "PAID");
            stats.put("paid", paidCount);
            
            // 已取消
            Long cancelledCount = orderDao.countOrdersByMemberIdAndStatus(memberId, "CANCELLED");
            stats.put("cancelled", cancelledCount);
            
        } catch (Exception e) {
            System.err.println("取得訂單統計失敗: " + e.getMessage());
            // 回傳預設值
            stats.put("total", 0L);
            stats.put("pending", 0L);
            stats.put("paid", 0L);
            stats.put("cancelled", 0L);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getOrderDetailWithEventInfo(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            BuyerOrderDC order = orderDao.findOrderById(orderId);
            if (order == null) {
                return result;
            }
            
            // 基本訂單資訊
            result.put("orderId", order.getOrderId());
            result.put("eventId", order.getEventId());
            result.put("memberId", order.getMemberId());
            result.put("totalAmount", order.getTotalAmount());
            result.put("orderStatus", order.getOrderStatus());
            result.put("isPaid", order.getIsPaid());
            
            // 格式化時間
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            if (order.getOrderTime() != null) {
                result.put("orderTime", sdf.format(order.getOrderTime()));
            }
            if (order.getCreateTime() != null) {
                result.put("createTime", sdf.format(order.getCreateTime()));
            }
            
            // TODO: 這裡可以加入活動資訊的查詢
            // 需要注入 EventService 來查詢活動詳細資訊
            
        } catch (Exception e) {
            System.err.println("取得訂單詳細資訊失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getOrderListWithEventInfo(Integer memberId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<BuyerOrderDC> orders = orderDao.findOrdersByMemberId(memberId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            
            for (BuyerOrderDC order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                
                // 基本訂單資訊
                orderInfo.put("orderId", order.getOrderId());
                orderInfo.put("eventId", order.getEventId());
                orderInfo.put("totalAmount", order.getTotalAmount());
                orderInfo.put("orderStatus", order.getOrderStatus());
                orderInfo.put("isPaid", order.getIsPaid());
                
                // 格式化時間
                if (order.getOrderTime() != null) {
                    orderInfo.put("orderTime", sdf.format(order.getOrderTime()));
                }
                if (order.getCreateTime() != null) {
                    orderInfo.put("createTime", sdf.format(order.getCreateTime()));
                }
                
                // TODO: 加入活動資訊
                // 暫時使用預設值
                orderInfo.put("eventName", "活動名稱");
                orderInfo.put("eventFromDate", "2024/03/15");
                orderInfo.put("place", "活動地點");
                orderInfo.put("ticketQuantity", 1);
                
                // 模擬票券明細
                List<Map<String, Object>> tickets = new ArrayList<>();
                Map<String, Object> ticket1 = new HashMap<>();
                ticket1.put("ticketNumber", "123456781");
                ticket1.put("categoryName", "一般區");
                ticket1.put("seatNumber", "1");
                ticket1.put("price", "1,100");
                tickets.add(ticket1);
                
                Map<String, Object> ticket2 = new HashMap<>();
                ticket2.put("ticketNumber", "123456782");
                ticket2.put("categoryName", "搖滾區");
                ticket2.put("seatNumber", "2");
                ticket2.put("price", "2,800");
                tickets.add(ticket2);
                
                orderInfo.put("tickets", tickets);
                
                result.add(orderInfo);
            }
            
        } catch (Exception e) {
            System.err.println("取得訂單列表失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}