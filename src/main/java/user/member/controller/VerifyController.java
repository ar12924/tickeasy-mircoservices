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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import common.vo.Core;

@RestController
@RequestMapping("member/verify")
public class VerifyController {

	@Autowired
	private MemberService memberService;

	@GetMapping
	public Core<Void> verify(@RequestParam("token") String token) {
		Core<Void> core = new Core<>();
		boolean result = memberService.activateMemberByToken(token);
		if (result) {
			core.setSuccessful(true);
			core.setMessage("驗證成功");
		} else {
			core.setSuccessful(false);
			core.setMessage("驗證失敗，請確認連結是否有效");
		}
		return core;
	}
}
