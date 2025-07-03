package user.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import user.member.service.MemberService;
import user.member.vo.Member;

@RestController
@RequestMapping("user/member/login")
public class LoginController {
	@Autowired
	private MemberService service;

	@GetMapping("{username}/{password}")
	public Member loginByGet(HttpServletRequest request, @PathVariable String username, @PathVariable String password) {
		Member member = new Member();
		if (username == null || password == null) {
			member.setMessage("無會員資訊");
			member.setSuccessful(false);
			return member;
		}
		member.setUserName(username);
		member.setPassword(password);
		member = service.login(member);
		if (member.isSuccessful()) {
			if (request.getSession(false) != null) {
				request.changeSessionId();
			}
			final HttpSession session = request.getSession();
			session.setAttribute("loggedin", true);
			session.setAttribute("member", member);
		}
		return member;
	}
}
