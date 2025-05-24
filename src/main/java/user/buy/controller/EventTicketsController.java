package user.buy.controller;

import com.google.gson.Gson;

import common.util.CommonUtil;
import user.buy.service.EventInfoService;
import user.buy.service.impl.EventInfoServiceImpl;
import user.buy.vo.TicketTypeVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活動票券類型控制器
 * 創建日期: 2025-05-12
 */
@WebServlet("/api/event/tickets/*")
public class EventTicketsController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private EventInfoService eventService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        eventService = CommonUtil.getBean(getServletContext(), EventInfoService.class);
        gson = new Gson();
        System.out.println("EventTicketsController 初始化完成");
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("EventTicketsController.doGet() 被調用");
        System.out.println("請求 URI: " + req.getRequestURI());
        
        // 設置響應類型
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // 獲取請求路徑
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        
        // 檢查路徑是否為空
        if (pathInfo == null || pathInfo.equals("/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("errorCode", "B0009");
            response.put("errorMessage", "無效的請求路徑");
            response.put("userMessage", "請提供有效的活動ID");
            out.println(gson.toJson(response));
            return;
        }
        
        // 從路徑中提取 eventId - 新的路徑格式為 /api/event/tickets/{eventId}
        String eventIdStr = pathInfo.substring(1); // 移除開頭的 /
        System.out.println("提取到的 eventId: " + eventIdStr);
        
        // 檢查是否包含額外的路徑段
        if (eventIdStr.contains("/")) {
            eventIdStr = eventIdStr.split("/")[0]; // 只取第一段作為 eventId
        }
        
        if (!isNumeric(eventIdStr)) {
            System.out.println("無法從路徑中獲取有效的 eventId");
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("errorCode", "B0011");
            response.put("errorMessage", "無效的活動ID");
            response.put("userMessage", "請提供有效的活動ID");
            out.println(gson.toJson(response));
            return;
        }
        
        try {
            Integer eventId = Integer.parseInt(eventIdStr);
            System.out.println("解析到 eventId: " + eventId);
            
            List<TicketTypeVO> ticketTypes = eventService.getEventTicketTypes(eventId);
            System.out.println("獲取到票券類型數量: " + (ticketTypes != null ? ticketTypes.size() : "null"));
            
            if (ticketTypes != null) {
                for (TicketTypeVO type : ticketTypes) {
                    System.out.println("票券類型: " + type.getTypeId() + ", 名稱: " + type.getCategoryName() + 
                                      ", 價格: " + type.getPrice() + ", 剩餘: " + type.getRemainingTickets());
                }
            }
            
            if (ticketTypes != null && !ticketTypes.isEmpty()) {
                // 將結果轉換為JSON並返回
                Map<String, Object> response = new HashMap<>();
                response.put("status", 200);
                response.put("data", formatTicketTypesForJson(ticketTypes));
                String jsonResponse = gson.toJson(response);
                System.out.println("返回 JSON: " + jsonResponse);
                out.println(jsonResponse);
            } else {
                // 沒有找到票券類型
                Map<String, Object> response = new HashMap<>();
                response.put("status", 404);
                response.put("errorCode", "B0010");
                response.put("errorMessage", "找不到票券類型");
                response.put("userMessage", "該活動尚未設置票券類型");
                out.println(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            // 活動ID格式錯誤
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("errorCode", "B0011");
            response.put("errorMessage", "無效的活動ID");
            response.put("userMessage", "請提供有效的活動ID");
            out.println(gson.toJson(response));
        } catch (Exception e) {
            System.out.println("處理請求時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", 500);
            response.put("errorCode", "B0012");
            response.put("errorMessage", "處理請求時發生錯誤");
            response.put("userMessage", "系統忙碌中，請稍後再試");
            out.println(gson.toJson(response));
        }
    }
    
    /**
     * 格式化票券類型列表，處理日期格式問題
     */
    private List<Map<String, Object>> formatTicketTypesForJson(List<TicketTypeVO> ticketTypes) {
        List<Map<String, Object>> formattedTicketTypes = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (TicketTypeVO ticketType : ticketTypes) {
            Map<String, Object> ticketTypeMap = new HashMap<>();
            
            ticketTypeMap.put("typeId", ticketType.getTypeId());
            ticketTypeMap.put("categoryName", ticketType.getCategoryName());
            ticketTypeMap.put("sellFromTime", ticketType.getSellFromTime() != null ? sdf.format(ticketType.getSellFromTime()) : null);
            ticketTypeMap.put("sellToTime", ticketType.getSellToTime() != null ? sdf.format(ticketType.getSellToTime()) : null);
            ticketTypeMap.put("price", ticketType.getPrice());
            ticketTypeMap.put("capacity", ticketType.getCapacity());
            ticketTypeMap.put("eventId", ticketType.getEventId());
            ticketTypeMap.put("remainingTickets", ticketType.getRemainingTickets());
            
            formattedTicketTypes.add(ticketTypeMap);
        }
        
        return formattedTicketTypes;
    }
    
    /**
     * 檢查字符串是否為數字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }
}