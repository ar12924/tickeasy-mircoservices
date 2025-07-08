package common.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import common.vo.AuthStatus;
import common.vo.Core;
import common.vo.DataStatus;
import user.member.vo.Member;

@RestController
@RequestMapping("common")
public class CommonController {
	/**
	 * 查 Session 驗證有沒有會員資料。
	 * 
	 * @return {Core<Member>} 回應操作結果。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("authenticate")
	public Core<Member> getMemberFromSession(@SessionAttribute(required = false) Member member) {
		var core = new Core<Member>();
		if (member == null) {
			core.setAuthStatus(AuthStatus.NOT_LOGGED_IN);
			core.setMessage("請先登入");
			core.setSuccessful(false);
			return core;
		}
		core.setAuthStatus(AuthStatus.LOGGED_IN);
		core.setMessage("已登入");
		core.setSuccessful(true);
		core.setDataStatus(DataStatus.FOUND);
		core.setData(member);
		return core;
	}
	
	/**
	 * 查 Session 驗證有沒有會員資料。
	 * 
	 * @return {Core<Member>} 回應操作結果。
	 */
	@CrossOrigin(origins = "*")
	@GetMapping("logout")
	public Core<String> logout(HttpSession session) {
		Core<String> core = new Core<>();
		
		session.invalidate();
		
		core.setAuthStatus(AuthStatus.LOGOUT);
		core.setMessage("登出了");
		core.setSuccessful(true);
		return core;
	}
}
