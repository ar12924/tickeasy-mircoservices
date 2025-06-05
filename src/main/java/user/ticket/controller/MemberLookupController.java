package user.ticket.controller;

import com.google.gson.Gson;
import common.util.CommonUtil;
import user.ticket.service.MemberLookupService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 會員查詢控制器
 * 創建者: archchang
 * 創建日期: 2025-06-05
 */
@WebServlet("/api/members/nickname/*")
public class MemberLookupController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private MemberLookupService memberLookupService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        memberLookupService = CommonUtil.getBean(getServletContext(), MemberLookupService.class);
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                buildErrorResponse(out, 400, "A0001", "缺少暱稱參數", "請提供有效的會員暱稱");
                return;
            }

            String nickname = pathInfo.substring(1); // 移除開頭的 /
            nickname = URLDecoder.decode(nickname, "UTF-8");
            
            if (nickname.trim().isEmpty()) {
                buildErrorResponse(out, 400, "A0002", "暱稱不能為空", "請提供有效的會員暱稱");
                return;
            }

            handleGetMemberByNickname(nickname.trim(), out);
            
        } catch (Exception e) {
            e.printStackTrace();
            buildErrorResponse(out, 500, "B0001", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
        }
    }

    private void handleGetMemberByNickname(String nickname, PrintWriter out) {
        try {
            Map<String, Object> memberInfo = memberLookupService.getMemberByNickname(nickname);
            
            Map<String, Object> response = new HashMap<>();
            if (memberInfo != null) {
                response.put("success", true);
                response.put("data", memberInfo);
                response.put("message", "查詢成功");
            } else {
                response.put("success", false);
                response.put("data", null);
                response.put("message", "找不到該會員");
            }
            
            out.println(gson.toJson(response));
            
        } catch (Exception e) {
            e.printStackTrace();
            buildErrorResponse(out, 500, "B0002", "查詢會員資訊失敗: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
        }
    }

    private void buildErrorResponse(PrintWriter out, int httpStatus, String errorCode, String errorMessage, String userMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("httpStatus", httpStatus);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("errorMessage", errorMessage);
        errorResponse.put("userMessage", userMessage);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        
        out.println(gson.toJson(errorResponse));
    }
}
