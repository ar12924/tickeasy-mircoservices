//package user.order.service;
//
//import java.util.List;
//
//import common.vo.Core;
//import user.member.vo.Member;
//import user.order.vo.BuyerOrderDC;
//
//public interface OrderService {
////	顯示所有訂單紀錄
//	Core<List<BuyerOrderDC>> ShowOrders(Member member);
//	
//}


package user.order.service;

import java.util.List;
import java.util.Map;
import user.order.vo.BuyerOrderDC;

public interface OrderService {
    
    /**
     * 根據會員ID查詢訂單列表
     */
    public List<BuyerOrderDC> getOrdersByMemberId(Integer memberId);
    
    /**
     * 根據訂單ID查詢訂單
     */
    public BuyerOrderDC getOrderById(Integer orderId);
    
    /**
     * 根據會員ID和訂單狀態查詢訂單
     */
    public List<BuyerOrderDC> getOrdersByMemberIdAndStatus(Integer memberId, String orderStatus);
    
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
    public List<BuyerOrderDC> getOrdersByEventId(Integer eventId);
    
    /**
     * 取得會員訂單統計
     */
    public Map<String, Long> getOrderStatsByMemberId(Integer memberId);
    
    /**
     * 取得訂單詳細資訊（包含活動資訊）
     */
    public Map<String, Object> getOrderDetailWithEventInfo(Integer orderId);
    
    /**
     * 取得會員的訂單列表（包含活動資訊）
     */
    public List<Map<String, Object>> getOrderListWithEventInfo(Integer memberId);
}