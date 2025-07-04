package manager.eventdetail.controller;

import com.google.gson.Gson;
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

import static common.util.CommonUtilNora.*;

@WebServlet("/manager/eventdetail/participants/detail")
public class ParticipantDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ParticipantService participantService;

    @Override
    public void init() throws ServletException {
        participantService = CommonUtil.getBean(getServletContext(), ParticipantService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String ticketIdStr = req.getParameter("ticketId");
        if (ticketIdStr == null || ticketIdStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"缺少 ticketId 參數\"}");
            return;
        }

        try {
            Integer ticketId = Integer.parseInt(ticketIdStr);
            Map<String, Object> detailData = participantService.getParticipantDetail(ticketId);
            writePojo2Json(resp, detailData);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"ticketId 格式不正確\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}