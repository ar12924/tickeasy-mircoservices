package manager.eventdetail.controller;

import common.vo.Core;
import manager.eventdetail.service.EventInfoVOService;
import manager.eventdetail.service.TicketSalesService;
import manager.eventdetail.vo.EventInfoEventVer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import user.member.vo.Member;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/eventdetail/dashboard")
public class DashboardController {
    @Autowired
    private TicketSalesService ticketSalesService;
    @Autowired
    private EventInfoVOService eventInfoVOService;

    @GetMapping
    public Core<Object> dashboard(
            @RequestParam(value = "eventId", required = false) String eventIdStr,
            @RequestParam(value = "memberId", required = false) String memberIdStr,
            HttpSession session) {
        Core<Object> core = new Core<>();
        Member loginUser = (Member) session.getAttribute("member");
        if (loginUser == null) {
            core.setSuccessful(false);
            core.setMessage("未登入，請先登入");
            return core;
        }
        if (loginUser.getRoleLevel() != 2 && loginUser.getRoleLevel() != 3) {
            core.setSuccessful(false);
            core.setMessage("無權限訪問此功能");
            return core;
        }
        Integer loginMemberId = loginUser.getMemberId();
        try {
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                return handleDashboardData(eventIdStr, loginMemberId);
            }
            if (memberIdStr != null && !memberIdStr.trim().isEmpty()) {
                return handleEventsList(memberIdStr, loginMemberId);
            }
            // 預設查詢自己的活動列表
            return handleEventsList(loginMemberId.toString(), loginMemberId);
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("處理請求時發生錯誤：" + e.getMessage());
            return core;
        }
    }

    @GetMapping("/ticketTypeTrend")
    public Map<String, Object> getTicketTypeTrend(@RequestParam Integer eventId) {
        return ticketSalesService.getTicketTypeTrendData(eventId);
    }

    private Core<Object> handleEventsList(String memberIdStr, Integer loginMemberId) {
        Core<Object> core = new Core<>();
        Integer memberId;
        try {
            memberId = Integer.valueOf(memberIdStr);
        } catch (NumberFormatException e) {
            core.setSuccessful(false);
            core.setMessage("無效的會員ID格式");
            return core;
        }
        if (!memberId.equals(loginMemberId)) {
            core.setSuccessful(false);
            core.setMessage("無權限查詢其他會員的活動");
            return core;
        }
        List<EventInfoEventVer> events = eventInfoVOService.getEventsByMemberId(memberId);
        if (events == null) {
            core.setSuccessful(false);
            core.setMessage("查詢活動列表失敗");
            return core;
        }
        core.setSuccessful(true);
        core.setMessage("查詢成功");
        core.setData(events);
        return core;
    }

    private Core<Object> handleDashboardData(String eventIdStr, Integer loginMemberId) {
        Core<Object> core = new Core<>();
        Integer eventId;
        try {
            eventId = Integer.valueOf(eventIdStr);
        } catch (NumberFormatException e) {
            core.setSuccessful(false);
            core.setMessage("無效的活動ID格式");
            return core;
        }
        EventInfoEventVer event = eventInfoVOService.getEventById(eventId);
        if (event == null) {
            core.setSuccessful(false);
            core.setMessage("找不到指定的活動");
            return core;
        }
        if (!event.getMemberId().equals(loginMemberId)) {
            core.setSuccessful(false);
            core.setMessage("無權限查詢此活動");
            return core;
        }
        Map<String, Object> result = ticketSalesService.getTicketSalesStatus(eventId);
        if (result == null) {
            core.setSuccessful(false);
            core.setMessage("查詢失敗：無資料");
            return core;
        }
        core.setSuccessful(true);
        core.setMessage("查詢成功");
        core.setData(result);
        return core;
    }
} 