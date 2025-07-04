package manager.eventdetail.controller;

import common.util.CommonUtil;
import manager.eventdetail.service.ParticipantService;
import manager.eventdetail.service.EventInfoVOService;
import manager.eventdetail.vo.EventTicketType;
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

@WebServlet("/manager/eventdetail/participants")
public class ParticipantListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ParticipantService participantService;
    private EventInfoVOService eventInfoVOService;

    @Override
    public void init() throws ServletException {
        participantService = CommonUtil.getBean(getServletContext(), ParticipantService.class);
        eventInfoVOService = CommonUtil.getBean(getServletContext(), EventInfoVOService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("ParticipantListController.doGet 被調用");
            System.out.println("請求參數 - eventId: " + req.getParameter("eventId") + ", memberId: " + req.getParameter("memberId"));
            
            // 0. 權限驗證：活動方只能查詢自己活動
            Member loginUser = (Member) req.getSession().getAttribute("member");
            if (loginUser == null) {
                System.out.println("權限驗證失敗：未登入（session中無member）");
                writeError(resp, "請先登入");
                return;
            }
            if (loginUser.getRoleLevel() != 2) {
                System.out.println("權限驗證失敗：非活動方角色（roleLevel != 2）");
                writeError(resp, "只有活動方可以查看此頁面");
                return;
            }
            Integer loginMemberId = loginUser.getMemberId();
            System.out.println("登入用戶 memberId: " + loginMemberId);

            // 檢查請求類型
            String eventIdStr = req.getParameter("eventId");
            String memberIdStr = req.getParameter("memberId");
            
            // 如果有 memberId，返回活動列表
            if (memberIdStr != null && !memberIdStr.trim().isEmpty()) {
                System.out.println("處理活動列表請求");
                handleEventsList(req, resp, memberIdStr, loginMemberId);
                return;
            }
            
            // 如果有 eventId，返回參與者列表
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                System.out.println("處理參與者列表請求");
                handleParticipantsList(req, resp, eventIdStr, loginMemberId);
                return;
            }
            
            // 如果都沒有，返回錯誤
            System.out.println("沒有提供必要的參數");
            writeError(resp, "請提供活動ID或會員ID");
        } catch (Exception e) {
            System.err.println("ParticipantListController.doGet 發生異常: " + e.getMessage());
            e.printStackTrace();
            writeError(resp, "處理請求時發生錯誤：" + e.getMessage());
        }
    }
    
    private void handleEventsList(HttpServletRequest req, HttpServletResponse resp, String memberIdStr, Integer loginMemberId) throws IOException {
        try {
            // 直接使用 loginMemberId 進行查詢
            List<EventInfoEventVer> events = eventInfoVOService.getEventsByMemberId(loginMemberId);
            if (events == null) {
                writeError(resp, "查詢活動列表失敗");
                return;
            }
            writeSuccess(resp, "查詢成功", events);
        } catch (Exception e) {
            e.printStackTrace();
            writeError(resp, "查詢活動列表時發生內部錯誤：" + e.getMessage());
        }
    }
    
    private void handleParticipantsList(HttpServletRequest req, HttpServletResponse resp, String eventIdStr, Integer loginMemberId) throws IOException {
        try {
            System.out.println("開始處理參與者列表請求，eventId: " + eventIdStr);
            
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
            searchParams.put("pageNumber", req.getParameter("pageNumber") != null ? Integer.parseInt(req.getParameter("pageNumber")) : 1);
            searchParams.put("pageSize", req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10);
            
            if (req.getParameter("participantName") != null && !req.getParameter("participantName").isEmpty()) {
                searchParams.put("participantName", req.getParameter("participantName"));
            }
            if (req.getParameter("email") != null && !req.getParameter("email").isEmpty()) {
                searchParams.put("email", req.getParameter("email"));
            }
            if (req.getParameter("phone") != null && !req.getParameter("phone").isEmpty()) {
                searchParams.put("phone", req.getParameter("phone"));
            }
            if (req.getParameter("status") != null && !req.getParameter("status").isEmpty()) {
                searchParams.put("status", Integer.parseInt(req.getParameter("status")));
            }
            if (req.getParameter("ticketTypeId") != null && !req.getParameter("ticketTypeId").isEmpty()) {
                searchParams.put("ticketTypeId", Integer.parseInt(req.getParameter("ticketTypeId")));
            }
            if (req.getParameter("isUsed") != null && !req.getParameter("isUsed").isEmpty()) {
                searchParams.put("isUsed", Integer.parseInt(req.getParameter("isUsed")));
            }
            
            System.out.println("查詢參數準備完成: " + searchParams);

            // 4. 執行查詢
            Map<String, Object> result;
            try {
                System.out.println("開始查詢參與者數據");
                result = participantService.searchParticipants(eventId, searchParams);
                if (result == null) {
                    System.out.println("查詢結果為 null");
                    writeError(resp, "查詢失敗：無資料");
                    return;
                }
                System.out.println("參與者數據查詢成功，結果大小: " + result.size());
            } catch (Exception e) {
                System.err.println("查詢參與者數據時發生異常: " + e.getMessage());
                e.printStackTrace();
                writeError(resp, "查詢失敗：" + e.getMessage());
                return;
            }

            // 5. 獲取票種列表
            try {
                System.out.println("開始獲取票種列表");
                List<EventTicketType> ticketTypes = participantService.getEventTicketTypes(eventId);
                result.put("ticketTypes", ticketTypes);
                System.out.println("票種列表獲取成功，數量: " + ticketTypes.size());
            } catch (Exception e) {
                System.err.println("獲取票種列表時發生異常: " + e.getMessage());
                e.printStackTrace();
                writeError(resp, "獲取票種列表失敗：" + e.getMessage());
                return;
            }

            System.out.println("準備返回成功響應");
            writeSuccess(resp, "查詢成功", result);
            System.out.println("成功響應已發送");
        } catch (Exception e) {
            System.err.println("handleParticipantsList 發生異常: " + e.getMessage());
            e.printStackTrace();
            writeError(resp, "處理請求時發生錯誤：" + e.getMessage());
        }
    }
} 

