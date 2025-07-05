package user.member.controller;

import java.util.Objects;
import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import common.vo.Core;
import user.member.vo.Member;
import user.member.service.MemberService;
import user.member.dao.VerificationDao;
import user.member.vo.VerificationToken;
import user.member.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("user/member/edit")
public class EditController {
	@Autowired
	private MemberService service;
	@Autowired
	private VerificationDao verifyDao;
	@Autowired
	private MailService mailService;

	// 查詢會員資料
	@GetMapping
	public Core<Member> getInfo(@SessionAttribute(required = false) Member member) {
		Core<Member> core = new Core<>();
		if (member == null) {
			core.setMessage("無會員資訊");
			core.setSuccessful(false);
		} else {
			core.setSuccessful(true);
			core.setData(member);
		}
		return core;
	}

	// 驗證舊密碼
	// @GetMapping("{password}")
	// public Core<Void> checkPassword(@PathVariable String password, @SessionAttribute(required = false) Member member) {
	// 	Core<Void> core = new Core<>();
	// 	if (member == null) {
	// 		core.setMessage("無會員資訊");
	// 		core.setSuccessful(false);
	// 	} else {
	// 		final String currentPassword = member.getPassword();
	// 		if (Objects.equals(password, currentPassword)) {
	// 			core.setSuccessful(true);
	// 		} else {
	// 			core.setMessage("舊密碼錯誤");
	// 			core.setSuccessful(false);
	// 		}
	// 	}
	// 	return core;
	// }


	// 修改會員資料（支援大頭貼上傳）
	@PostMapping(value = "update", consumes = {"multipart/form-data"})
	public Core<Member> edit(
			@RequestParam("member") String memberJson,
			@RequestPart(value = "photo", required = false) MultipartFile photo,
			@SessionAttribute Member member) {
		Core<Member> core = new Core<>();
		try {
			// 使用 ObjectMapper 解析 JSON
			ObjectMapper mapper = new ObjectMapper();
			Member reqMember = mapper.readValue(memberJson, Member.class);
			
			reqMember.setMemberId(member.getMemberId());
			reqMember.setUserName(member.getUserName());
			
			if (photo != null && !photo.isEmpty()) {
				reqMember.setPhoto(photo.getBytes());
			}
			
			Member updated = service.editMember(reqMember);
			
			core.setSuccessful(true);
			core.setMessage("會員資料已更新");
			core.setData(updated);
		} catch (Exception e) {
			core.setSuccessful(false);
			core.setMessage("會員資料更新失敗：" + e.getMessage());
		}
		return core;
	}

	// 驗證信 API
	@PostMapping("send-verify-mail")
	public Core<Void> sendVerifyMail(@SessionAttribute Member member) {
		Core<Void> core = new Core<>();
		try {
			// 產生新 token
			String tokenName = UUID.randomUUID().toString();
			VerificationToken token = new VerificationToken();
			token.setTokenName(tokenName);
			token.setTokenType("EMAIL_VERIFY");
			token.setExpiredTime(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 24小時
			token.setMember(member);
			verifyDao.insert(token);

			// 寄信
			mailService.sendActivationNotification(member.getEmail(), member.getUserName(), tokenName);

			core.setSuccessful(true);
			core.setMessage("驗證信已發送，請至信箱收信");
		} catch (Exception e) {
			core.setSuccessful(false);
			core.setMessage("發送失敗：" + e.getMessage());
		}
		return core;
	}
}
