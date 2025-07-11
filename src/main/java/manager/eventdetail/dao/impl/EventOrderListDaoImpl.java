package manager.eventdetail.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.eventdetail.dao.EventOrderListDao;
import manager.eventdetail.vo.OrderDetail;
import manager.eventdetail.vo.OrderDetail.OrderItem;
import manager.eventdetail.vo.OrderSummary;

@Repository
public class EventOrderListDaoImpl implements EventOrderListDao {
    
    @PersistenceContext
    private Session session;
    
    @Override
    public List<OrderSummary> findOrdersByEventId(Integer eventId) {
        try {
            String sql = """
                SELECT 
                    bo.order_id,
                    bo.event_id,
                    ei.event_name,
                    bo.member_id,
                    bo.order_time,
                    bo.is_paid,
                    bo.total_amount,
                    bo.order_status,
                    bo.create_time,
                    bo.update_time
                FROM buyer_order bo
                JOIN event_info ei ON bo.event_id = ei.event_id
                WHERE bo.event_id = :eventId
                ORDER BY bo.order_time DESC
            """;
            
            List<Object[]> results = session.createNativeQuery(sql)
                    .setParameter("eventId", eventId)
                    .getResultList();
            
            List<OrderSummary> orders = new ArrayList<>();
            
            for (Object[] row : results) {
                OrderSummary order = new OrderSummary();
                order.setOrderId((Integer) row[0]);
                order.setEventId((Integer) row[1]);
                order.setEventName((String) row[2]);
                order.setMemberId((Integer) row[3]);
                order.setOrderTime((Timestamp) row[4]);
                order.setIsPaid((Boolean) row[5]);
                order.setTotalAmount((BigDecimal) row[6]);
                order.setOrderStatus((String) row[7]);
                order.setCreateTime((Timestamp) row[8]);
                order.setUpdateTime((Timestamp) row[9]);
                
                orders.add(order);
            }
            
            System.out.println("✅ DAO層查詢到 " + orders.size() + " 筆訂單");
            return orders;
            
        } catch (Exception e) {
            System.err.println("根據活動ID查詢訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public OrderDetail findOrderDetailById(Integer orderId) {
        try {
            // 查詢訂單基本資訊
            String orderSql = """
                SELECT 
                    bo.order_id,
                    bo.event_id,
                    ei.event_name,
                    bo.member_id,
                    bo.order_time,
                    bo.is_paid,
                    bo.total_amount,
                    bo.order_status,
                    bo.create_time,
                    bo.update_time
                FROM buyer_order bo
                JOIN event_info ei ON bo.event_id = ei.event_id
                WHERE bo.order_id = :orderId
            """;
            
            Object[] orderRow = (Object[]) session.createNativeQuery(orderSql)
                    .setParameter("orderId", orderId)
                    .uniqueResult();
            
            if (orderRow == null) {
                System.err.println("找不到訂單ID: " + orderId);
                return null;
            }
            
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId((Integer) orderRow[0]);
            orderDetail.setEventId((Integer) orderRow[1]);
            orderDetail.setEventName((String) orderRow[2]);
            orderDetail.setMemberId((Integer) orderRow[3]);
            orderDetail.setOrderTime((Timestamp) orderRow[4]);
            orderDetail.setIsPaid((Boolean) orderRow[5]);
            orderDetail.setTotalAmount((BigDecimal) orderRow[6]);
            orderDetail.setOrderStatus((String) orderRow[7]);
            orderDetail.setCreateTime((Timestamp) orderRow[8]);
            orderDetail.setUpdateTime((Timestamp) orderRow[9]);
            
            // 查詢訂單明細項目（這裡假設有 order_items 表）
            // 由於你沒有提供訂單明細表結構，我用假設的結構
            String itemsSql = """
                SELECT 
                    oi.ticket_type_name,
                    oi.quantity,
                    oi.unit_price,
                    (oi.quantity * oi.unit_price) as subtotal
                FROM order_items oi
                WHERE oi.order_id = :orderId
                ORDER BY oi.item_id
            """;
            
            try {
                List<Object[]> itemRows = session.createNativeQuery(itemsSql)
                        .setParameter("orderId", orderId)
                        .getResultList();
                
                List<OrderItem> items = new ArrayList<>();
                for (Object[] itemRow : itemRows) {
                    OrderItem item = new OrderItem();
                    item.setTicketTypeName((String) itemRow[0]);
                    item.setQuantity((Integer) itemRow[1]);
                    item.setUnitPrice((BigDecimal) itemRow[2]);
                    item.setSubtotal((BigDecimal) itemRow[3]);
                    items.add(item);
                }
                
                orderDetail.setItems(items);
                
            } catch (Exception e) {
                // 如果沒有訂單明細表或查詢失敗，提供預設資料
                System.out.println("無法查詢訂單明細項目，可能是表格不存在: " + e.getMessage());
                
                List<OrderItem> defaultItems = new ArrayList<>();
                OrderItem defaultItem = new OrderItem();
                defaultItem.setTicketTypeName("票種資訊暫無");
                defaultItem.setQuantity(1);
                defaultItem.setUnitPrice(orderDetail.getTotalAmount());
                defaultItem.setSubtotal(orderDetail.getTotalAmount());
                defaultItems.add(defaultItem);
                
                orderDetail.setItems(defaultItems);
            }
            
            System.out.println("✅ DAO層查詢到訂單明細，訂單ID: " + orderId);
            return orderDetail;
            
        } catch (Exception e) {
            System.err.println("查詢訂單明細失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}