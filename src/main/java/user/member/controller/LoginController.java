package user.member.controller;

import common.vo.AuthStatus;
import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import user.member.service.MemberService;
import user.member.vo.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user/member/login")
public class LoginController {
	@Autowired
	private MemberService service;

	@GetMapping("{username}/{password}")
	public Core<Member> loginByGet(HttpServletRequest request, 
	@PathVariable String username, @PathVariable String password) {
		Core<Member> core = new Core<>();
		if (username == null || password == null) {
			core.setMessage("無會員資訊");
			core.setSuccessful(false);
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			return core;
		}
		
		Member member = new Member();
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
			
			core.setSuccessful(true);
			core.setMessage("登入成功");
			core.setData(member);
			core.setAuthStatus(AuthStatus.LOGGED_IN);
		} else {
			core.setSuccessful(false);
			core.setMessage(member.getMessage());
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
		}
		return core;
	}
}
