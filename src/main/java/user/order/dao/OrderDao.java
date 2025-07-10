//package user.order.dao;
//
//import java.util.List;
//
//import common.dao.CommonDao;
//import user.order.vo.BuyerOrderDC;
//
//public interface OrderDao extends CommonDao {
//
//	public List<BuyerOrderDC> findAllOrders(Integer memberId);
//
//}


package user.order.dao;

import java.util.List;
import common.dao.CommonDao;
import user.order.vo.BuyerOrderDC;

public interface OrderDao extends CommonDao {
    
    /**
     * 根據會員ID查詢訂單列表
     */
    public List<BuyerOrderDC> findOrdersByMemberId(Integer memberId);
    
    /**
     * 根據訂單ID查詢訂單
     */
    public BuyerOrderDC findOrderById(Integer orderId);
    
    /**
     * 根據會員ID和訂單狀態查詢訂單
     */
    public List<BuyerOrderDC> findOrdersByMemberIdAndStatus(Integer memberId, String orderStatus);
    
    /**
     * 新增訂單
     */
    public int createOrder(BuyerOrderDC order);
    
    /**
     * 更新訂單
     */
    public int updateOrder(BuyerOrderDC order);
    
    /**
     * 更新訂單狀態
     */
    public int updateOrderStatus(Integer orderId, String orderStatus, Boolean isPaid);
    
    /**
     * 刪除訂單
     */
    public int deleteOrder(Integer orderId);
    
    /**
     * 根據活動ID查詢訂單
     */
    public List<BuyerOrderDC> findOrdersByEventId(Integer eventId);
    
    /**
     * 統計會員的訂單數量
     */
    public Long countOrdersByMemberId(Integer memberId);
    
    /**
     * 統計各狀態的訂單數量
     */
    public Long countOrdersByMemberIdAndStatus(Integer memberId, String orderStatus);
}