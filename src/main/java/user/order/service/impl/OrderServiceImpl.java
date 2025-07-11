package user.order.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.event.vo.MngEventInfo;
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
            // 使用新的方法查詢含活動資訊的訂單
            List<BuyerOrderDC> orders = orderDao.findOrdersWithEventInfo(memberId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            
            for (BuyerOrderDC order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                
                // 基本訂單資訊
                orderInfo.put("orderId", order.getOrderId());
                orderInfo.put("eventId", order.getEventId());
                orderInfo.put("memberId", order.getMemberId());
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
                
                // 從關聯的 MngEventInfo 取得真實的活動資訊（包含圖片）
                if (order.getMngEventInfo() != null) {
                    MngEventInfo eventInfo = order.getMngEventInfo();
                    orderInfo.put("eventName", eventInfo.getEventName());
                    orderInfo.put("place", eventInfo.getPlace());
                    
                    // 加入活動圖片處理
                    if (eventInfo.getImage() != null && eventInfo.getImage().length > 0) {
                        // 將 byte[] 轉換為 Base64 字串
                        String base64Image = Base64.getEncoder().encodeToString(eventInfo.getImage());
                        orderInfo.put("image", "data:image/jpeg;base64," + base64Image);
                    } else {
                        // 沒有圖片時使用預設圖片
                        orderInfo.put("eventImage", null);
                    }
                    
                    if (eventInfo.getEventFromDate() != null) {
                        orderInfo.put("eventFromDate", sdf.format(eventInfo.getEventFromDate()));
                    }
                } else {
                    System.err.println("訂單 " + order.getOrderId() + " 找不到對應的活動資訊");
                    orderInfo.put("eventName", "找不到活動資訊");
                    orderInfo.put("place", "找不到地點資訊");
                    orderInfo.put("eventFromDate", "");
                    orderInfo.put("eventImage", null); // 沒有活動時也要設定為 null
                }
                
                orderInfo.put("ticketQuantity", 1);
                
                // 票券明細
                List<Map<String, Object>> tickets = new ArrayList<>();
                Map<String, Object> ticket = new HashMap<>();
                ticket.put("ticketNumber", "T" + order.getOrderId() + "001");
                ticket.put("categoryName", "一般區");
                ticket.put("seatNumber", "1");
                ticket.put("price", order.getTotalAmount());
                tickets.add(ticket);
                orderInfo.put("tickets", tickets);
                
                result.add(orderInfo);
            }
            
            System.out.println("成功查詢到 " + result.size() + " 筆訂單資訊");
            
        } catch (Exception e) {
            System.err.println("取得會員訂單列表失敗: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}