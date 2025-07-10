//package user.order.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.SessionAttribute;
//
//import common.vo.AuthStatus;
//import common.vo.Core;
//import user.member.vo.Member;
//import user.order.service.OrderService;
//import user.order.vo.BuyerOrderDC;
//
//@RestController
//@RequestMapping("user/orders")
//@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501" })
//public class ShowOrderController {
//	@Autowired
//	private OrderService service;
//
//	@GetMapping
//	public Core<List<BuyerOrderDC>> showAllEvents(@SessionAttribute(required = false) Member member) {
//		var core = new Core<List<BuyerOrderDC>>();
//
//		if (member == null) {
//			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
//			core.setMessage("請先登入");
//			core.setSuccessful(false);
//			return core;
//		}
//		return service.ShowOrders(member);
//	}
//
//}
package user.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;
import user.order.service.OrderService;

@RestController
@RequestMapping("order")
@CrossOrigin(origins = { 
    "http://127.0.0.1:5500", 
    "http://127.0.0.1:5501", 
    "http://127.0.0.1:8080",
    "http://localhost:5500", 
    "http://localhost:5501", 
    "http://localhost:8080"
})
public class ShowOrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 取得會員的訂單列表
     */
    @PostMapping("/order-list")
    public Core<List<Map<String, Object>>> getOrderList(@RequestBody Map<String, Object> request) {
        Core<List<Map<String, Object>>> core = new Core<>();
        
        try {
            Integer memberId = (Integer) request.get("memberId");
            
            if (memberId == null || memberId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的會員ID");
                core.setData(List.of());
                return core;
            }
            
            List<Map<String, Object>> orderList = orderService.getOrderListWithEventInfo(memberId);
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(orderList);
            core.setCount((long) orderList.size());
            
        } catch (Exception e) {
            System.err.println("查詢訂單列表失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(List.of());
        }
        
        return core;
    }
    
    /**
     * 取得訂單統計資訊
     */
    @GetMapping("/stats/{memberId}")
    public Core<Map<String, Long>> getOrderStats(@PathVariable Integer memberId) {
        Core<Map<String, Long>> core = new Core<>();
        
        try {
            if (memberId == null || memberId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的會員ID");
                return core;
            }
            
            Map<String, Long> stats = orderService.getOrderStatsByMemberId(memberId);
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(stats);
            
        } catch (Exception e) {
            System.err.println("查詢訂單統計失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
        }
        
        return core;
    }
    
    /**
     * 取得特定訂單詳情
     */
    @GetMapping("/detail/{orderId}")
    public Core<Map<String, Object>> getOrderDetail(@PathVariable Integer orderId) {
        Core<Map<String, Object>> core = new Core<>();
        
        try {
            if (orderId == null || orderId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的訂單ID");
                return core;
            }
            
            Map<String, Object> orderDetail = orderService.getOrderDetailWithEventInfo(orderId);
            
            if (orderDetail.isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("找不到指定訂單");
                return core;
            }
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(orderDetail);
            
        } catch (Exception e) {
            System.err.println("查詢訂單詳情失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
        }
        
        return core;
    }
    
    /**
     * 根據狀態篩選訂單
     */
    @PostMapping("/filter-by-status")
    public Core<List<Map<String, Object>>> getOrdersByStatus(@RequestBody Map<String, Object> request) {
        Core<List<Map<String, Object>>> core = new Core<>();
        
        try {
            Integer memberId = (Integer) request.get("memberId");
            String orderStatus = (String) request.get("orderStatus");
            
            if (memberId == null || memberId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的會員ID");
                core.setData(List.of());
                return core;
            }
            
            List<Map<String, Object>> orderList;
            
            if (orderStatus == null || orderStatus.isEmpty() || "ALL".equals(orderStatus)) {
                // 查詢全部訂單
                orderList = orderService.getOrderListWithEventInfo(memberId);
            } else {
                // 根據狀態篩選
                orderList = orderService.getOrderListWithEventInfo(memberId);
                orderList = orderList.stream()
                    .filter(order -> orderStatus.equals(order.get("orderStatus")))
                    .toList();
            }
            
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(orderList);
            core.setCount((long) orderList.size());
            
        } catch (Exception e) {
            System.err.println("篩選訂單失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(List.of());
        }
        
        return core;
    }
    
    /**
     * 更新訂單狀態
     */
    @PostMapping("/update-status")
    public Core<Integer> updateOrderStatus(@RequestBody Map<String, Object> request) {
        Core<Integer> core = new Core<>();
        
        try {
            Integer orderId = (Integer) request.get("orderId");
            String orderStatus = (String) request.get("orderStatus");
            Boolean isPaid = (Boolean) request.get("isPaid");
            
            if (orderId == null || orderId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的訂單ID");
                return core;
            }
            
            if (orderStatus == null || orderStatus.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("訂單狀態不可為空");
                return core;
            }
            
            int result = orderService.updateOrderStatus(orderId, orderStatus, isPaid);
            
            if (result > 0) {
                core.setSuccessful(true);
                core.setMessage("訂單狀態更新成功");
                core.setData(orderId);
            } else {
                core.setSuccessful(false);
                core.setMessage("訂單狀態更新失敗");
            }
            
        } catch (Exception e) {
            System.err.println("更新訂單狀態失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("系統錯誤：" + e.getMessage());
        }
        
        return core;
    }
    
    /**
     * 取得活動的所有訂單
     */
    @GetMapping("/event/{eventId}")
    public Core<List<Map<String, Object>>> getOrdersByEventId(@PathVariable Integer eventId) {
        Core<List<Map<String, Object>>> core = new Core<>();
        
        try {
            if (eventId == null || eventId <= 0) {
                core.setSuccessful(false);
                core.setMessage("無效的活動ID");
                core.setData(List.of());
                return core;
            }
            
            // 這裡可以實作取得特定活動的所有訂單
            // 目前先回傳空列表
            core.setSuccessful(true);
            core.setMessage("查詢成功");
            core.setData(List.of());
            core.setCount(0L);
            
        } catch (Exception e) {
            System.err.println("查詢活動訂單失敗: " + e.getMessage());
            e.printStackTrace();
            
            core.setSuccessful(false);
            core.setMessage("查詢失敗：" + e.getMessage());
            core.setData(List.of());
        }
        
        return core;
    }
}