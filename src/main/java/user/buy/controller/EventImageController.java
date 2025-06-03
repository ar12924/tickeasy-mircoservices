package user.buy.controller;

import common.util.CommonUtil;
import user.buy.service.EventInfoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 活動圖片控制器，專門處理活動圖片相關請求
 * 創建者: archchang
 * 創建日期: 2025-06-03
 */
@WebServlet("/api/events/image/*")
public class EventImageController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private EventInfoService eventService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        eventService = CommonUtil.getBean(getServletContext(), EventInfoService.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 獲取路徑資訊
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的請求路徑");
            return;
        }
        
        // 從路徑中提取活動ID，路徑格式為 /api/events/image/{eventId}
        String eventIdStr = pathInfo.substring(1); // 移除開頭的 /
        
        try {
            Integer eventId = Integer.parseInt(eventIdStr);
            byte[] imageData = eventService.getEventImage(eventId);
            
            if (imageData != null && imageData.length > 0) {
                // 設置響應類型
                resp.setContentType("image/jpeg");
                resp.setContentLength(imageData.length);
                
                // 設置緩存控制
                resp.setHeader("Cache-Control", "public, max-age=3600");
                
                // 寫入圖片數據
                try (OutputStream out = resp.getOutputStream()) {
                    out.write(imageData);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "圖片不存在");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的活動ID格式");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "獲取圖片時發生錯誤");
        }
    }
}