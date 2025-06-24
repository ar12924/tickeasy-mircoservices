package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import user.member.service.MailService;
import user.member.service.MemberService;

import static common.util.CommonUtilNora.*;
import static common.util.CommonUtil.getBean;

@WebServlet("/user/member/verify")
public class VerifyController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService service;
	private MailService mailService;
	
	@Override
	public void init() throws ServletException {
		service = getBean(getServletContext(), MemberService.class);
		mailService = getBean(getServletContext(), MailService.class);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tokenStr = req.getParameter("token");
		boolean result = service.activateMemberByToken(tokenStr);

		if (result) {
			writeSuccess(resp, "驗證成功", null);
		} else {
			writeError(resp, "驗證失敗，請確認連結是否有效");
		}
	}

}
