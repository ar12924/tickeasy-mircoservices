package manager.eventdetail.controller;

import common.util.CommonUtil;
import manager.eventdetail.service.ParticipantService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static user.member.util.CommonUtil.writeError;
import static user.member.util.CommonUtil.writeSuccess;

@WebServlet("/manager/eventdetail/dashboard/data")
public class DashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ParticipantService participantService;

    @Override
    public void init() throws ServletException {
        participantService = CommonUtil.getBean(getServletContext(), ParticipantService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventIdStr = req.getParameter("eventId");
        if (eventIdStr == null || eventIdStr.trim().isEmpty()) {
            writeError(resp, "請提供活動ID");
            return;
        }

        Integer eventId;
        try {
            eventId = Integer.valueOf(eventIdStr);
        } catch (NumberFormatException e) {
            writeError(resp, "無效的活動ID格式");
            return;
        }

        try {
            Map<String, Object> dashboardData = participantService.getSalesDashboardData(eventId);
            if (dashboardData == null) {
                writeError(resp, "查詢儀表板資料失敗");
                return;
            }
            writeSuccess(resp, "查詢成功", dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            writeError(resp, "查詢儀表板資料時發生內部錯誤：" + e.getMessage());
        }
    }
} 