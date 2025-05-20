package user.member.controller;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.vo.Member;
import user.member.util.CommonUtil;


@WebServlet("/user/member/logout")
public class LogoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();
        Member result = new Member();
        result.setSuccessful(true);
        result.setMessage("已成功登出");
        CommonUtil.writePojo2Json(resp, result);
	}
}
