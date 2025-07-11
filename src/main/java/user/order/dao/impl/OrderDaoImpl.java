//package user.order.dao.impl;
//
//import java.util.List;
//
//import javax.persistence.PersistenceContext;
//
//import org.hibernate.Session;
//import org.springframework.stereotype.Repository;
//
//import user.order.dao.OrderDao;
//import user.order.vo.BuyerOrderDC;
//
//@Repository
//public class OrderDaoImpl implements OrderDao {
//
//	@PersistenceContext
//	private Session session;
//
//	@Override
//	public List<BuyerOrderDC> findAllOrders(Integer memberId) {
//		String hql = "FROM BuyerOrderDC WHERE memberId = :memberId ORDER BY order_id DESC";
//		return session.createQuery(hql, user.order.vo.BuyerOrderDC.class).getResultList();
//	}
//}

package user.order.dao.impl;

import java.util.List;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import user.order.dao.OrderDao;
import user.order.vo.BuyerOrderDC;

@Repository
public class OrderDaoImpl implements OrderDao {
    
    @PersistenceContext
    private Session session;
    
    @Override
    public List<BuyerOrderDC> findOrdersByMemberId(Integer memberId) {
        try {
            String hql = "FROM BuyerOrderDC WHERE memberId = :memberId ORDER BY createTime DESC";
            return session.createQuery(hql, BuyerOrderDC.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("查詢會員訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public BuyerOrderDC findOrderById(Integer orderId) {
        try {
            return session.get(BuyerOrderDC.class, orderId);
        } catch (Exception e) {
            System.err.println("查詢訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<BuyerOrderDC> findOrdersByMemberIdAndStatus(Integer memberId, String orderStatus) {
        try {
            String hql = "FROM BuyerOrderDC WHERE memberId = :memberId AND orderStatus = :orderStatus ORDER BY createTime DESC";
            return session.createQuery(hql, BuyerOrderDC.class)
                    .setParameter("memberId", memberId)
                    .setParameter("orderStatus", orderStatus)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("根據狀態查詢訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public int createOrder(BuyerOrderDC order) {
        try {
            session.persist(order);
            session.flush();
            System.out.println("✅ 訂單新增成功，ID: " + order.getOrderId());
            return 1;
        } catch (Exception e) {
            System.err.println("新增訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public int updateOrder(BuyerOrderDC order) {
        try {
            BuyerOrderDC existing = session.get(BuyerOrderDC.class, order.getOrderId());
            if (existing == null) {
                System.err.println("找不到訂單ID: " + order.getOrderId());
                return 0;
            }
            
            existing.setEventId(order.getEventId());
            existing.setMemberId(order.getMemberId());
            existing.setOrderTime(order.getOrderTime());
            existing.setIsPaid(order.getIsPaid());
            existing.setTotalAmount(order.getTotalAmount());
            existing.setOrderStatus(order.getOrderStatus());
            
            session.merge(existing);
            session.flush();
            System.out.println("✅ 訂單更新成功，ID: " + order.getOrderId());
            return 1;
        } catch (Exception e) {
            System.err.println("更新訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public int updateOrderStatus(Integer orderId, String orderStatus, Boolean isPaid) {
        try {
            String hql = "UPDATE BuyerOrderDC SET orderStatus = :orderStatus, isPaid = :isPaid WHERE orderId = :orderId";
            int result = session.createQuery(hql)
                    .setParameter("orderStatus", orderStatus)
                    .setParameter("isPaid", isPaid)
                    .setParameter("orderId", orderId)
                    .executeUpdate();
            
            System.out.println("✅ 訂單狀態更新成功，ID: " + orderId + ", 狀態: " + orderStatus);
            return result;
        } catch (Exception e) {
            System.err.println("更新訂單狀態失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public int deleteOrder(Integer orderId) {
        try {
            BuyerOrderDC order = session.get(BuyerOrderDC.class, orderId);
            if (order != null) {
                session.delete(order);
                session.flush();
                System.out.println("✅ 訂單刪除成功，ID: " + orderId);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            System.err.println("刪除訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public List<BuyerOrderDC> findOrdersByEventId(Integer eventId) {
        try {
            String hql = "FROM BuyerOrderDC WHERE eventId = :eventId ORDER BY createTime DESC";
            return session.createQuery(hql, BuyerOrderDC.class)
                    .setParameter("eventId", eventId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("根據活動ID查詢訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public Long countOrdersByMemberId(Integer memberId) {
        try {
            String hql = "SELECT COUNT(*) FROM BuyerOrderDC WHERE memberId = :memberId";
            return session.createQuery(hql, Long.class)
                    .setParameter("memberId", memberId)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("統計會員訂單數量失敗: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }
    
    @Override
    public List<BuyerOrderDC> findOrdersWithEventInfo(Integer memberId) {
        try {
            // ✅ 正確的 JOIN FETCH 查詢
            String hql = "SELECT o FROM BuyerOrderDC o " +
                        "LEFT JOIN FETCH o.mngEventInfo e " +
                        "WHERE o.memberId = :memberId " +
                        "ORDER BY o.createTime DESC";
            
            return session.createQuery(hql, BuyerOrderDC.class)
                    .setParameter("memberId", memberId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("查詢會員訂單含活動資訊失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public Long countOrdersByMemberIdAndStatus(Integer memberId, String orderStatus) {
        try {
            String hql = "SELECT COUNT(*) FROM BuyerOrderDC WHERE memberId = :memberId AND orderStatus = :orderStatus";
            return session.createQuery(hql, Long.class)
                    .setParameter("memberId", memberId)
                    .setParameter("orderStatus", orderStatus)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("統計特定狀態訂單數量失敗: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }
}