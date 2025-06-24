package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.member.service.MemberService;

import static common.util.CommonUtilNora.*;
import static common.util.CommonUtil.getBean;

@WebServlet("/user/member/logout")
public class LogoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService service;
	
	@Override
	public void init() throws ServletException {
		service = getBean(getServletContext(), MemberService.class);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
			writeSuccess(resp, "登出成功", null);
		} else {
			writeError(resp, "尚未登入");
		}
	}
}
