package manager.eventdetail.controller;

import common.util.CommonUtil;
import manager.eventdetail.service.ParticipantService;
import manager.eventdetail.vo.EventTicketType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static user.member.util.CommonUtil.*;


@WebServlet("/participants")
public class ParticipantListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ParticipantService participantService;

    @Override
    public void init() throws ServletException {
        participantService = CommonUtil.getBean(getServletContext(), ParticipantService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 檢查必要參數
        String eventIdStr = req.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            writeError(resp, "請提供活動ID");
            return;
        }

        // 2. 解析參數
        Integer eventId;
        try {
            eventId = Integer.valueOf(eventIdStr);
        } catch (NumberFormatException e) {
            writeError(resp, "無效的活動ID格式");
            return;
        }

        // 3. 準備查詢參數
        Map<String, Object> searchParams;
        try {
            searchParams = parseSearchParams(req);
        } catch (NumberFormatException e) {
            writeError(resp, "無效的查詢參數格式");
            return;
        }

        // 4. 執行查詢
        Map<String, Object> result;
        try {
            result = participantService.searchParticipants(eventId, searchParams);
            if (result == null) {
                writeError(resp, "查詢失敗：無資料");
                return;
            }
        } catch (Exception e) {
            writeError(resp, "查詢失敗：" + e.getMessage());
            return;
        }

        // 5. 獲取票種列表
        try {
            List<EventTicketType> ticketTypes = participantService.getEventTicketTypes(eventId);
            result.put("ticketTypes", ticketTypes);
        } catch (Exception e) {
            writeError(resp, "獲取票種列表失敗：" + e.getMessage());
            return;
        }

        writeSuccess(resp, "查詢成功", result);
    }

    private Map<String, Object> parseSearchParams(HttpServletRequest req) {
        Map<String, Object> searchParams = new HashMap<>();

        // 分頁參數
        searchParams.put("pageNumber", parseIntOrDefault(req.getParameter("pageNumber"), 1));
        searchParams.put("pageSize", parseIntOrDefault(req.getParameter("pageSize"), 10));

        // 搜尋條件
        addParamIfNotEmpty(searchParams, "participantName", req.getParameter("participantName"));
        addParamIfNotEmpty(searchParams, "email", req.getParameter("email"));
        addParamIfNotEmpty(searchParams, "phone", req.getParameter("phone"));
        addParamIfNotEmpty(searchParams, "status", req.getParameter("status"), Integer::valueOf);
        addParamIfNotEmpty(searchParams, "ticketTypeId", req.getParameter("ticketTypeId"), Integer::valueOf);
        addParamIfNotEmpty(searchParams, "orderStatus", req.getParameter("orderStatus"), Integer::valueOf);
        addParamIfNotEmpty(searchParams, "isUsed", req.getParameter("isUsed"), Integer::valueOf);

        return searchParams;
    }

    private void addParamIfNotEmpty(Map<String, Object> params, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value);
        }
    }

    private void addParamIfNotEmpty(Map<String, Object> params, String key, String value, java.util.function.Function<String, Object> converter) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                params.put(key, converter.apply(value));
            } catch (NumberFormatException e) {
                // 忽略無效的數值轉換
            }
        }
    }

    private int parseIntOrDefault(String str, int defaultVal) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultVal;
        }
    }
} 

