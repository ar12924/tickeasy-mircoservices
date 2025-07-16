//package manager.eventdetail.controller;
//
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import common.vo.Core;
//import manager.eventdetail.service.EventOrderListService;
//import manager.eventdetail.vo.OrderDetail;
//import manager.eventdetail.vo.OrderSummary;
//
//@RestController
//@RequestMapping("manager")
//@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080",
//        "http://localhost:5500", "http://localhost:5501", "http://localhost:8080" })
//public class EventOrderListController {
//
//    @Autowired
//    private EventOrderListService orderService;
//
//    /**
//     * 根據活動ID取得訂單列表
//     */
//    @GetMapping("/orders/event/{eventId}")
//    public Core<List<OrderSummary>> getOrdersByEventId(@PathVariable Integer eventId) {
//        System.out.println("=== 查詢活動訂單列表 ===");
//        System.out.println("活動ID: " + eventId);
//        
//        Core<List<OrderSummary>> core = new Core<>();
//        
//        try {
//            if (eventId == null || eventId <= 0) {
//                core.setSuccessful(false);
//                core.setMessage("無效的活動ID");
//                return core;
//            }
//            
//            List<OrderSummary> orders = orderService.findOrdersByEventId(eventId);
//            
//            System.out.println("✅ 查詢到 " + orders.size() + " 筆訂單");
//            
//            core.setSuccessful(true);
//            core.setMessage("查詢成功");
//            core.setData(orders);
//            core.setCount((long) orders.size());
//            
//        } catch (Exception e) {
//            System.err.println("❌ 查詢訂單失敗: " + e.getMessage());
//            e.printStackTrace();
//            
//            core.setSuccessful(false);
//            core.setMessage("查詢失敗：" + e.getMessage());
//            core.setData(List.of());
//            core.setCount(0L);
//        }
//        
//        return core;
//    }
//    
//    /**
//     * 根據訂單ID取得訂單明細
//     */
//    @GetMapping("/orders/{orderId}/detail")
//    public Core<OrderDetail> getOrderDetail(@PathVariable Integer orderId) {
//        System.out.println("=== 查詢訂單明細 ===");
//        System.out.println("訂單ID: " + orderId);
//        
//        Core<OrderDetail> core = new Core<>();
//        
//        try {
//            if (orderId == null || orderId <= 0) {
//                core.setSuccessful(false);
//                core.setMessage("無效的訂單ID");
//                return core;
//            }
//            
//            OrderDetail orderDetail = orderService.findOrderDetailById(orderId);
//            
//            if (orderDetail != null) {
//                System.out.println("✅ 查詢到訂單明細");
//                core.setSuccessful(true);
//                core.setMessage("查詢成功");
//                core.setData(orderDetail);
//                core.setCount(1L);
//            } else {
//                System.err.println("❌ 找不到訂單ID: " + orderId);
//                core.setSuccessful(false);
//                core.setMessage("找不到指定的訂單");
//                core.setData(null);
//                core.setCount(0L);
//            }
//            
//        } catch (Exception e) {
//            System.err.println("❌ 查詢訂單明細失敗: " + e.getMessage());
//            e.printStackTrace();
//            
//            core.setSuccessful(false);
//            core.setMessage("查詢失敗：" + e.getMessage());
//            core.setData(null);
//            core.setCount(0L);
//        }
//        
//        return core;
//    }
//}


package manager.eventdetail.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import manager.eventdetail.service.EventOrderListService;
import manager.eventdetail.vo.OrderDetail;
import manager.eventdetail.vo.OrderSummary;

@RestController
@RequestMapping("manager")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://127.0.0.1:5501", "http://127.0.0.1:8080",
        "http://localhost:5500", "http://localhost:5501", "http://localhost:8080" })
public class EventOrderListController {

    @Autowired
    private EventOrderListService orderService;

    /**
     * 根據活動ID取得訂單列表
     */
    @GetMapping("/orders/event/{eventId}")
    public List<OrderSummary> getOrdersByEventId(@PathVariable Integer eventId) {
        System.out.println("=== 查詢活動訂單列表 ===");
        System.out.println("活動ID: " + eventId);
        
        try {
            if (eventId == null || eventId <= 0) {
                System.err.println("❌ 無效的活動ID: " + eventId);
                return List.of();
            }
            
            List<OrderSummary> orders = orderService.findOrdersByEventId(eventId);
            
            System.out.println("✅ 查詢到 " + orders.size() + " 筆訂單");
            return orders;
            
        } catch (Exception e) {
            System.err.println("❌ 查詢訂單失敗: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * 根據訂單ID取得訂單明細
     */
    @GetMapping("/orders/{orderId}/detail")
    public OrderDetail getOrderDetail(@PathVariable Integer orderId) {
        System.out.println("=== 查詢訂單明細 ===");
        System.out.println("訂單ID: " + orderId);
        
        try {
            if (orderId == null || orderId <= 0) {
                System.err.println("❌ 無效的訂單ID: " + orderId);
                return null;
            }
            
            OrderDetail orderDetail = orderService.findOrderDetailById(orderId);
            
            if (orderDetail != null) {
                System.out.println("✅ 查詢到訂單明細");
                return orderDetail;
            } else {
                System.err.println("❌ 找不到訂單ID: " + orderId);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ 查詢訂單明細失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}