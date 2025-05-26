package user.ticket.controller;

import common.util.CommonUtil;
import user.ticket.dao.SwapPostDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 會員照片控制器
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
@WebServlet("/api/member-photos/*")
public class MemberPhotoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private SwapPostDao swapPostDao;

    @Override
    public void init() throws ServletException {
        super.init();
        swapPostDao = CommonUtil.getBean(getServletContext(), SwapPostDao.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "請提供會員ID");
            return;
        }

        String memberIdStr = pathInfo.substring(1); // 移除開頭的 /
        
        try {
            Integer memberId = Integer.parseInt(memberIdStr);
            
            if (memberId <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的會員ID");
                return;
            }

            byte[] photoData = swapPostDao.getMemberPhoto(memberId);
            
            if (photoData != null && photoData.length > 0) {
                resp.setContentType("image/jpeg");
                resp.setContentLength(photoData.length);
                resp.setHeader("Cache-Control", "max-age=3600");
                resp.getOutputStream().write(photoData);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "找不到會員照片");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的會員ID格式");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "獲取照片時發生錯誤");
        }
    }
}