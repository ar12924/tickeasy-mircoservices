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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("user/member/verify")
public class VerifyController {

	@Autowired
	private MemberService memberService;

	@GetMapping
	@Transactional
	public String verify(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
		boolean result = memberService.activateMemberByToken(token);
		if (result) {
			// 驗證成功，重定向到登入頁面並帶上成功訊息
			return "redirect:/user/member/login.html?verified=true";
		} else {
			// 驗證失敗，重定向到登入頁面並帶上失敗訊息
			return "redirect:/user/member/login.html?verified=false";
		}
	}
}
