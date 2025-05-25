package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.util.CommonUtil;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.vo.Member;
import static user.member.util.CommonUtil.*;

@WebServlet("/user/member/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService service;
	private MailService mailService;
	
	@Override
	public void init() throws ServletException {
		service = CommonUtil.getBean(getServletContext(), MemberService.class);
		mailService = CommonUtil.getBean(getServletContext(), MailService.class);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Member member = json2Pojo(req, Member.class);
		if (member == null) {
			writeError(resp, "無會員資料");
			return;
		}

		member = service.login(member);
		if (member.isSuccessful()) {
			Member full = service.getByUsername(member.getUserName());

			if (req.getSession(false) != null) {
				req.changeSessionId();
			}
			HttpSession session = req.getSession();
			session.setAttribute("loggedin", true);
			session.setAttribute("member", full);
			writeSuccess(resp, "登入成功", full);
		} else {
			writeError(resp, member.getMessage());
		}
	}

}
