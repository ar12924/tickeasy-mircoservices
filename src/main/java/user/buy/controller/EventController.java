package user.buy.controller;

import com.google.gson.Gson;

import common.util.CommonUtil;
import user.buy.service.EventInfoService;
import user.buy.service.impl.EventInfoServiceImpl;
import user.buy.vo.EventBuyVO;
import user.buy.vo.TicketTypeVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活動控制器，處理活動相關RESTful API請求
 * 創建者: archchang
 * 創建日期: 2025-05-07
 */
@WebServlet("/api/events/*")
public class EventController extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
    private EventInfoService eventService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        super.init();
        eventService = CommonUtil.getBean(getServletContext(), EventInfoService.class);
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 獲取請求路徑
        String pathInfo = req.getPathInfo();
        
        // 處理圖片請求，路徑格式為 /api/events/{eventId}/image
        if (pathInfo != null && pathInfo.endsWith("/image")) {
            handleImageRequest(req, resp);
            return;
        }
        
        // 設置響應類型為JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        PrintWriter out = resp.getWriter();
        
        // 獲取當前登入用戶的ID
        Integer memberId = getMemberIdFromSession(req);
        
        Map<String, Object> response = new HashMap<>();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            handleRecommendedEvents(req, response, memberId);
        } else {
            // 移除搜索邏輯，直接處理活動詳情
            handleEventDetail(pathInfo, response, memberId);
        }
        
        out.println(gson.toJson(response));
    }
    
    /**
     * 處理圖片請求
     */
    private void handleImageRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String eventIdStr = pathInfo.substring(1, pathInfo.lastIndexOf("/"));
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            byte[] imageData = eventService.getEventImage(eventId);
            
            if (imageData != null && imageData.length > 0) {
                // 設置響應類型
                resp.setContentType("image/jpeg"); // 可以根據實際圖片類型調整
                resp.setContentLength(imageData.length);
                
                // 寫入圖片數據
                try (OutputStream out = resp.getOutputStream()) {
                    out.write(imageData);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "圖片不存在");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的活動ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "獲取圖片時發生錯誤");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 設置響應類型
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // 獲取請求路徑和參數
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        
        // 獲取當前登入用戶的ID
        Integer memberId = getMemberIdFromSession(req);
        
        // 檢查用戶是否已登入
        if (memberId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 401);
            response.put("errorCode", "A0001");
            response.put("errorMessage", "用戶未登入");
            response.put("userMessage", "請先登入");
            out.println(gson.toJson(response));
            return;
        }
        
        // 根據請求路徑處理不同請求
        if (pathInfo != null && pathInfo.endsWith("/favorite")) {
            // 設置活動關注狀態，路徑格式為 /api/events/{eventId}/favorite
            String eventIdPart = pathInfo.substring(1, pathInfo.lastIndexOf("/"));
            
            try {
                Integer eventId = Integer.parseInt(eventIdPart);
                Integer isFollowed = getIntParameter(req, "isFollowed", 1); // 預設為關注
                
                boolean success = eventService.toggleEventFavorite(memberId, eventId, isFollowed);
                
                if (success) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 200);
                    out.println(gson.toJson(response));
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", 500);
                    response.put("errorCode", "B0006");
                    response.put("errorMessage", "設置關注狀態失敗");
                    response.put("userMessage", "無法設置關注狀態，請稍後再試");
                    out.println(gson.toJson(response));
                }
            } catch (NumberFormatException e) {
                // 活動ID格式錯誤
                Map<String, Object> response = new HashMap<>();
                response.put("status", 400);
                response.put("errorCode", "B0007");
                response.put("errorMessage", "無效的活動ID");
                response.put("userMessage", "請提供有效的活動ID");
                out.println(gson.toJson(response));
            }
        } else {
            // 不支援的請求
            Map<String, Object> response = new HashMap<>();
            response.put("status", 404);
            response.put("errorCode", "B0008");
            response.put("errorMessage", "不支援的請求");
            response.put("userMessage", "不支援的請求路徑");
            out.println(gson.toJson(response));
        }
    }
    
    /**
     * 格式化活動對象，處理日期格式問題
     */
    private Map<String, Object> formatEventForJson(EventBuyVO event) {
        Map<String, Object> eventMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        eventMap.put("eventId", event.getEventId());
        eventMap.put("eventName", event.getEventName());
        eventMap.put("eventFromDate", event.getEventFromDate() != null ? sdf.format(event.getEventFromDate()) : null);
        eventMap.put("eventToDate", event.getEventToDate() != null ? sdf.format(event.getEventToDate()) : null);
        eventMap.put("eventHost", event.getEventHost());
        eventMap.put("totalCapacity", event.getTotalCapacity());
        eventMap.put("place", event.getPlace());
        eventMap.put("summary", event.getSummary());
        eventMap.put("detail", event.getDetail());
        eventMap.put("isPosted", event.getPosted());
        eventMap.put("imageDir", event.getImageDir());
        eventMap.put("memberId", event.getMemberId());
        eventMap.put("remainingTickets", event.getRemainingTickets());
        eventMap.put("isFollowed", event.getFollowed());
        
        return eventMap;
    }
    
    /**
     * 格式化活動列表，處理日期格式問題
     */
    private List<Map<String, Object>> formatEventListForJson(List<EventBuyVO> events) {
        List<Map<String, Object>> formattedEvents = new ArrayList<>();
        
        for (EventBuyVO event : events) {
            formattedEvents.add(formatEventForJson(event));
        }
        
        return formattedEvents;
    }
    
    /**
     * 從請求中獲取整數參數，如果參數不存在或格式錯誤，則使用預設值
     */
    private int getIntParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String paramValue = req.getParameter(paramName);
        if (paramValue == null || paramValue.trim().isEmpty()) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 從Session中獲取當前登入用戶的ID
     */
    private Integer getMemberIdFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object memberId = session.getAttribute("memberId");
            if (memberId instanceof Integer) {
                return (Integer) memberId;
            }
        }
        return null;
    }
    
    /**
     * 處理推薦活動請求
     */
    private void handleRecommendedEvents(HttpServletRequest req, Map<String, Object> response, Integer memberId) {
        int limit = getIntParameter(req, "limit", 10);
        List<EventBuyVO> events = eventService.getRecommendedEvents(limit, memberId);
        
        response.put("status", 200);
        response.put("data", formatEventListForJson(events));
    }
    
    /**
     * 處理活動詳情請求
     */
    private void handleEventDetail(String pathInfo, Map<String, Object> response, Integer memberId) {
        String eventIdStr = pathInfo.substring(1); // 移除開頭的 /
        
        try {
            Integer eventId = Integer.parseInt(eventIdStr);
            EventBuyVO event = eventService.getEventDetail(eventId, memberId);
            
            if (event != null) {
                response.put("status", 200);
                response.put("data", formatEventForJson(event));
            } else {
                response.put("status", 404);
                response.put("errorCode", "B0004");
                response.put("errorMessage", "活動不存在");
                response.put("userMessage", "找不到對應的活動");
            }
        } catch (NumberFormatException e) {
            response.put("status", 400);
            response.put("errorCode", "B0005");
            response.put("errorMessage", "無效的活動ID");
            response.put("userMessage", "請提供有效的活動ID");
        }
    }
}