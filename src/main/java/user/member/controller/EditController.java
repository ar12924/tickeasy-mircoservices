package user.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import common.util.CommonUtil;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.vo.Member;

import static user.member.util.CommonUtil.*;
//import static user.member.util.MemberConstants.SERVICE;

@WebServlet("/user/member/edit")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 5 * 1024 * 1024, // 5MB
		maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class EditController extends HttpServlet {
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

		// 1. 取得session中已登入會員
		final HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute("member") == null) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeError(resp, "請先登入");
			return;
		}
		Member login = (Member) session.getAttribute("member");

		// 後續確認：使用BeanUtils.populate()去把 request 裡的資料自動對應，去少寫setXXX
		Member input = new Member();
		input.setMemberId(login.getMemberId()); 
		input.setUserName(login.getUserName());
		input.setPassword(req.getParameter("password"));
		input.setNickName(req.getParameter("nickName"));
		input.setEmail(req.getParameter("email"));
		input.setUnicode(req.getParameter("unicode"));

		Part photoPart = req.getPart("photo");
		if (photoPart != null && photoPart.getSize() > 0) {
			input.setPhoto(photoPart.getInputStream().readAllBytes());
		}

		Member updated = service.editMember(input);
		writeSuccess(resp, "會員資料已更新", updated);
	}
}
