package user.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import user.member.dao.VerificationDao;
import user.member.service.MemberService;
import user.member.vo.Member;
import user.member.vo.VerificationToken;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

@RestController
@RequestMapping("user/member/edit")
public class EditController {
    @Autowired
    private MemberService service;

    @Autowired
    private VerificationDao verifyDao;

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
    public Core<Member> edit(@RequestParam("member") String memberJson, @RequestPart(value = "photo", required = false) MultipartFile photo, @SessionAttribute Member member) {
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
    public Core<Member> sendVerifyMail(@SessionAttribute Member member) {
        Core<Member> core = new Core<>();
        try {
            Member result = service.sendVerificationMail(member);
            if (result.isSuccessful()) {
                core.setSuccessful(true);
                core.setMessage("驗證信已發送，請至信箱收信");
            } else {
                core.setSuccessful(false);
                core.setMessage(result.getMessage());
            }
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage(e.getMessage());
        }
        return core;
    }

    // 發送密碼更新認證信 API
    @PostMapping("send-password-update-mail")
    public Core<Member> sendPasswordUpdateMail(@RequestParam String newPassword, @SessionAttribute Member member) {
        Core<Member> core = new Core<>();
        try {
            // 驗證密碼長度
            if (newPassword == null || newPassword.length() < 6) {
                core.setSuccessful(false);
                core.setMessage("密碼長度至少需要6個字元");
                return core;
            }
            // 發送密碼更新認證信（新密碼直接傳給 service）
            Member result = service.sendPasswordUpdateMail(member, newPassword);
            if (result.isSuccessful()) {
                core.setSuccessful(true);
                core.setMessage("密碼更新認證信已發送，請至信箱收信確認");
            } else {
                core.setSuccessful(false);
                core.setMessage(result.getMessage());
            }
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("發送認證信失敗：" + e.getMessage());
        }
        return core;
    }

    // 處理密碼更新認證
    @GetMapping("verify-password-update")
    @Transactional
    public void verifyPasswordUpdate(@RequestParam String token, HttpSession session, HttpServletResponse response) throws IOException {
        try {
            System.out.println("開始處理密碼更新認證，token: " + token);

            // 用 token 前綴查詢 VerificationToken
            VerificationToken verificationToken = verifyDao.findByTokenPrefix(token);
            if (verificationToken == null) {
                System.err.println("找不到對應的 token: " + token);
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=invalid_token");
                return;
            }

            if (verificationToken.getExpiredTime().before(new Timestamp(System.currentTimeMillis()))) {
                System.err.println("Token 已過期: " + verificationToken.getExpiredTime());
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=expired_token");
                return;
            }

            if (!"PASSWORD_UPDATE".equals(verificationToken.getTokenType())) {
                System.err.println("Token 類型不正確: " + verificationToken.getTokenType());
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=invalid_token_type");
                return;
            }

            // 檢查 member 是否存在
            Member member = verificationToken.getMember();
            if (member == null) {
                System.err.println("Token 對應的會員為 null");
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=member_not_found");
                return;
            }

            System.out.println("找到會員: " + member.getMemberId() + ", " + member.getUserName());

            // 解析 tokenName，取得加密密碼
            String tokenName = verificationToken.getTokenName();
            String[] parts = tokenName.split("\\|");
            if (parts.length != 2) {
                System.err.println("Token 格式錯誤");
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=token_format");
                return;
            }
            String encryptedPassword = parts[1];

            System.out.println("準備更新會員密碼，會員ID: " + member.getMemberId());

            // 更新會員密碼並刪除token（事務性操作）
            Member result = service.updatePasswordAndDeleteToken(member, encryptedPassword, verificationToken.getTokenId());

            if (result.isSuccessful()) {
                System.out.println("密碼更新成功");
                // 更新 session 中的會員資料
                session.setAttribute("member", result);
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?success=password_updated");
            } else {
                System.err.println("密碼更新失敗: " + result.getMessage());
                response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=update_failed");
            }
        } catch (Exception e) {
            System.err.println("密碼更新認證錯誤: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/maven-tickeasy-v1/user/member/edit.html?error=system_error");
        }
    }
}
