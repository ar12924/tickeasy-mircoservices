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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpSession;
import common.vo.Core;

@RestController
@RequestMapping("/manager/eventdetail/participants")
public class ParticipantListController {
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private EventInfoVOService eventInfoVOService;

    @GetMapping
    public Core<Object> participants(
            @RequestParam(value = "eventId", required = false) String eventIdStr,
            @RequestParam(value = "memberId", required = false) String memberIdStr,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "participantName", required = false) String participantName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "ticketTypeId", required = false) Integer ticketTypeId,
            @RequestParam(value = "isUsed", required = false) Integer isUsed,
            HttpSession session) {
        Core<Object> core = new Core<>();
        Member loginUser = (Member) session.getAttribute("member");
        if (loginUser == null) {
            core.setSuccessful(false);
            core.setMessage("請先登入");
            return core;
        }
        if (loginUser.getRoleLevel() != 2 && loginUser.getRoleLevel() != 3) {
            core.setSuccessful(false);
            core.setMessage("只有活動方可以查看此頁面");
            return core;
        }
        Integer loginMemberId = loginUser.getMemberId();
        try {
            if (memberIdStr != null && !memberIdStr.trim().isEmpty()) {
                return handleEventsList(loginMemberId);
            }
            if (eventIdStr != null && !eventIdStr.trim().isEmpty()) {
                return handleParticipantsList(eventIdStr, pageNumber, pageSize, participantName, email, phone, status, ticketTypeId, isUsed, loginMemberId);
            }
            core.setSuccessful(false);
            core.setMessage("請提供活動ID或會員ID");
            return core;
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("處理請求時發生錯誤：" + e.getMessage());
            return core;
        }
    }

    private Core<Object> handleEventsList(Integer loginMemberId) {
        Core<Object> core = new Core<>();
        List<EventInfoEventVer> events = eventInfoVOService.getEventsByMemberId(loginMemberId);
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

    private Core<Object> handleParticipantsList(String eventIdStr, Integer pageNumber, Integer pageSize, String participantName, String email, String phone, Integer status, Integer ticketTypeId, Integer isUsed, Integer loginMemberId) {
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
        // 組查詢參數
        Map<String, Object> searchParams = new java.util.HashMap<>();
        if (pageNumber != null) searchParams.put("pageNumber", pageNumber);
        if (pageSize != null) searchParams.put("pageSize", pageSize);
        if (participantName != null) searchParams.put("participantName", participantName);
        if (email != null) searchParams.put("email", email);
        if (phone != null) searchParams.put("phone", phone);
        if (status != null) searchParams.put("status", status);
        if (ticketTypeId != null) searchParams.put("ticketTypeId", ticketTypeId);
        if (isUsed != null) searchParams.put("isUsed", isUsed);
        Map<String, Object> result = participantService.searchParticipants(eventId, searchParams);
        if (result == null) {
            core.setSuccessful(false);
            core.setMessage("查詢失敗：無資料");
            return core;
        }
        List<EventTicketType> ticketTypes = participantService.getEventTicketTypes(eventId);
        result.put("ticketTypes", ticketTypes);
        core.setSuccessful(true);
        core.setMessage("查詢成功");
        core.setData(result);
        return core;
    }
} 

