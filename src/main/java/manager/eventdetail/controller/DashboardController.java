package manager.eventdetail.controller;

import common.util.CommonUtil;
import manager.eventdetail.service.TicketSalesService;
import manager.eventdetail.service.EventInfoVOService;
import manager.eventdetail.vo.EventInfoEventVer;
import user.member.vo.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.util.CommonUtilNora.*;

@WebServlet("/manager/eventdetail/dashboard")
public class DashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TicketSalesService ticketSalesService;
    private EventInfoVOService eventInfoVOService;

    @Override
    public void init() throws ServletException {
        System.out.println("=== DashboardController.init() 被調用 ===");
        System.out.println("Servlet名稱: " + getServletName());
        System.out.println("Servlet路徑: " + getServletContext().getContextPath());
        try {
            ticketSalesService = CommonUtil.getBean(getServletContext(), TicketSalesService.class);
            eventInfoVOService = CommonUtil.getBean(getServletContext(), EventInfoVOService.class);
            System.out.println("Service注入成功");
        } catch (Exception e) {
            System.err.println("Service注入失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("DashboardController.doGet 被調用");
            System.out.println("請求 URL: " + req.getRequestURL());
            System.out.println("請求參數: " + req.getQueryString());
            
            // 權限驗證
            Member loginUser = (Member) req.getSession().getAttribute("member");
            System.out.println("Session中的用戶: " + (loginUser != null ? loginUser.getUserName() : "null"));
            System.out.println("Session ID: " + req.getSession().getId());
            System.out.println("Session是否為新創建: " + req.getSession().isNew());
            
            if (loginUser == null) {
                System.out.println("權限驗證失敗：用戶未登入");
                writeError(resp, "未登入，請先登入");
                return;
            }
            
            if (loginUser.getRoleLevel() != 2) {
                System.out.println("權限驗證失敗：用戶權限不足，roleLevel=" + loginUser.getRoleLevel());
                writeError(resp, "無權限訪問此功能");
                return;
            }
            
            Integer loginMemberId = loginUser.getMemberId();
            System.out.println("登入用戶 memberId: " + loginMemberId);

            String eventIdStr = req.getParameter("eventId");
            String memberIdStr = req.getParameter("memberId");
            
            // 如果有 eventId，返回儀表板數據
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                System.out.println("處理儀表板數據請求");
                handleDashboardData(req, resp, eventIdStr, loginMemberId);
                return;
            }
            
            // 如果有 memberId，返回活動列表
            if (memberIdStr != null && !memberIdStr.trim().isEmpty()) {
                System.out.println("處理活動列表請求");
                handleEventsList(req, resp, memberIdStr, loginMemberId);
                return;
            }
            
            // 如果都沒有，使用session中的用戶ID查詢活動列表
            System.out.println("沒有提供參數，使用session中的用戶ID查詢活動列表");
            handleEventsList(req, resp, loginMemberId.toString(), loginMemberId);
        } catch (Exception e) {
            System.err.println("DashboardController.doGet 發生異常: " + e.getMessage());
            e.printStackTrace();
            writeError(resp, "處理請求時發生錯誤：" + e.getMessage());
        }
    }
    
    private void handleEventsList(HttpServletRequest req, HttpServletResponse resp, String memberIdStr, Integer loginMemberId) throws IOException {
        try {
            System.out.println("handleEventsList 開始執行");
            
            // 驗證 memberId 是否為當前登入用戶
            Integer memberId;
            try {
                memberId = Integer.valueOf(memberIdStr);
                System.out.println("解析後的 memberId: " + memberId);
            } catch (NumberFormatException e) {
                System.out.println("memberId 格式錯誤: " + memberIdStr);
                writeError(resp, "無效的會員ID格式");
                return;
            }
            
            // 檢查權限：用戶只能查詢自己的活動
            if (!memberId.equals(loginMemberId)) {
                System.out.println("權限檢查失敗: memberId=" + memberId + ", loginMemberId=" + loginMemberId);
                writeError(resp, "無權限查詢其他會員的活動");
                return;
            }

            System.out.println("開始查詢活動列表");
            List<EventInfoEventVer> events = eventInfoVOService.getEventsByMemberId(memberId);
            System.out.println("查詢結果: " + (events != null ? events.size() + " 個活動" : "null"));
            
            if (events == null) {
                System.out.println("查詢活動列表失敗");
                writeError(resp, "查詢活動列表失敗");
                return;
            }
            
            System.out.println("準備發送成功響應");
            writeSuccess(resp, "查詢成功", events);
            System.out.println("成功響應已發送");
        } catch (Exception e) {
            System.err.println("handleEventsList 發生異常: " + e.getMessage());
            e.printStackTrace();
            writeError(resp, "查詢活動列表時發生內部錯誤：" + e.getMessage());
        }
    }
    
    private void handleDashboardData(HttpServletRequest req, HttpServletResponse resp, String eventIdStr, Integer loginMemberId) throws IOException {
        try {
            System.out.println("開始處理儀表板數據請求，eventId: " + eventIdStr);
            
            // 1. 檢查必要參數
            if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
                System.out.println("eventId 為空");
                writeError(resp, "請提供活動ID");
                return;
            }

            // 2. 解析參數
            Integer eventId;
            try {
                eventId = Integer.valueOf(eventIdStr);
                System.out.println("解析後的 eventId: " + eventId);
            } catch (NumberFormatException e) {
                System.out.println("eventId 格式錯誤: " + eventIdStr);
                writeError(resp, "無效的活動ID格式");
                return;
            }

            // 2.5 驗證 eventId 是否屬於該活動方
            System.out.println("開始查詢活動信息");
            EventInfoEventVer event = eventInfoVOService.getEventById(eventId);
            if (event == null) {
                System.out.println("找不到指定的活動");
                writeError(resp, "找不到指定的活動");
                return;
            }
            if (!event.getMemberId().equals(loginMemberId)) {
                System.out.println("無權限查詢此活動，活動的 memberId: " + event.getMemberId() + ", 登入用戶 memberId: " + loginMemberId);
                writeError(resp, "無權限查詢此活動");
                return;
            }
            System.out.println("活動驗證通過");

            // 3. 準備查詢參數
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("pageNumber", 1);
            searchParams.put("pageSize", 10);
            System.out.println("查詢參數準備完成");

            // 4. 執行查詢
            Map<String, Object> result;
            try {
                System.out.println("開始查詢票券銷售數據");
                result = ticketSalesService.getTicketSalesStatus(eventId);
                if (result == null) {
                    System.out.println("查詢結果為 null");
                    writeError(resp, "查詢失敗：無資料");
                    return;
                }
                System.out.println("票券銷售數據查詢成功，結果大小: " + result.size());
            } catch (Exception e) {
                System.err.println("查詢票券銷售數據時發生異常: " + e.getMessage());
                e.printStackTrace();
                writeError(resp, "查詢失敗：" + e.getMessage());
                return;
            }

            System.out.println("準備返回成功響應");
            writeSuccess(resp, "查詢成功", result);
            System.out.println("成功響應已發送");
        } catch (Exception e) {
            System.err.println("handleDashboardData 發生異常: " + e.getMessage());
            e.printStackTrace();
            writeError(resp, "處理請求時發生錯誤：" + e.getMessage());
        }
    }
} 