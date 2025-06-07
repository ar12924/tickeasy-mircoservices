package manager.eventdetail.controller;

import common.util.CommonUtil;
import manager.eventdetail.service.ParticipantService;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static user.member.util.CommonUtil.*;

@WebServlet("/participants/detail")
public class ParticipantDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ParticipantService participantService;

    @Override
    public void init() throws ServletException {
        participantService = CommonUtil.getBean(getServletContext(), ParticipantService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 檢查必要參數
        String ticketIdStr = req.getParameter("ticketId");
        if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
            writeError(resp, "請提供票券ID");
            return;
        }

        // 2. 解析參數
        Long ticketId;
        try {
            ticketId = Long.valueOf(ticketIdStr);
        } catch (NumberFormatException e) {
            writeError(resp, "無效的票券ID格式");
            return;
        }

        // 3. 執行查詢
        Map<String, Object> result;
        try {
            result = participantService.getParticipantDetail(ticketId);
            if (result == null || !(Boolean) result.get("success")) {
                writeError(resp, (String) result.get("msg"));
                return;
            }
        } catch (Exception e) {
            writeError(resp, "查詢失敗：" + e.getMessage());
            return;
        }

        // 4. 獲取票種列表
        BuyerTicketEventVer ticket = (BuyerTicketEventVer) result.get("ticket");
        if (ticket != null && ticket.getEventTicketType() != null) {
            Integer eventId = ticket.getEventTicketType().getEventId();
            if (eventId != null) {
                try {
                    List<EventTicketType> ticketTypes = participantService.getEventTicketTypes(eventId);
                    result.put("ticketTypes", ticketTypes);
                } catch (Exception e) {
                    writeError(resp, "獲取票種列表失敗：" + e.getMessage());
                    return;
                }
            }

            writeSuccess(resp, "查詢成功", result);
        }
    }
}