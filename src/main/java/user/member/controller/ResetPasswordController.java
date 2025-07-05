package user.member.controller;

import java.util.UUID;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import common.vo.Core;
import user.member.service.MemberService;
import user.member.service.MailService;
import user.member.vo.Member;
import user.member.vo.VerificationToken;
import user.member.dao.VerificationDao;

@RestController
@RequestMapping("user/member/reset-password")
public class ResetPasswordController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationDao verifyDao;

    // 請求密碼重置
    @PostMapping("request")
    @Transactional
    public Core<Object> requestPasswordReset(@RequestParam String email) {
        Core<Object> core = new Core<>();

        try {
            // 根據 email 查找會員
            Member member = memberService.getByEmail(email);
            if (member == null) {
                core.setSuccessful(false);
                core.setMessage("找不到此 email 對應的會員帳號");
                return core;
            }

            // 產生重置 token
            String tokenName = UUID.randomUUID().toString();
            VerificationToken token = new VerificationToken();
            token.setTokenName(tokenName);
            token.setTokenType("RESET_PASSWORD");
            token.setExpiredTime(new Timestamp(System.currentTimeMillis() + 3600 * 1000)); // 1小時
            token.setMember(member);
            verifyDao.insert(token);

            // 發送重置郵件
            mailService.sendPasswordResetNotification(member.getEmail(), member.getNickName(), tokenName);

            core.setSuccessful(true);
            core.setMessage("密碼重置郵件已發送，請檢查您的信箱");
            return core;

        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("發送密碼重置郵件失敗：" + e.getMessage());
            return core;
        }
    }

    // 驗證重置 token
    @GetMapping("verify")
    public ResponseEntity<Core<Object>> verifyResetToken(@RequestParam String token) {
        Core<Object> core = new Core<>();

        try {
            VerificationToken resetToken = verifyDao.findByToken(token);
            if (resetToken == null ||
                    resetToken.getExpiredTime().before(new Timestamp(System.currentTimeMillis())) ||
                    !"RESET_PASSWORD".equals(resetToken.getTokenType())) {
                core.setSuccessful(false);
                core.setMessage("無效或已過期的重置連結");
                return ResponseEntity.badRequest().body(core);
            }

            Member member = resetToken.getMember();
            Member safeMember = new Member();
            safeMember.setMemberId(member.getMemberId());
            safeMember.setUserName(member.getUserName());
            safeMember.setNickName(member.getNickName());
            safeMember.setEmail(member.getEmail());

            core.setSuccessful(true);
            core.setMessage("Token 驗證成功");
            core.setData(safeMember);
            return ResponseEntity.ok(core);

        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("驗證失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(core);
        }
    }

    // 執行密碼重置
    @PostMapping("reset")
    @Transactional
    public Core<Object> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Core<Object> core = new Core<>();

        try {
            VerificationToken resetToken = verifyDao.findByToken(token);
            if (resetToken == null ||
                    resetToken.getExpiredTime().before(new Timestamp(System.currentTimeMillis())) ||
                    !"RESET_PASSWORD".equals(resetToken.getTokenType())) {
                core.setSuccessful(false);
                core.setMessage("無效或已過期的重置連結");
                return core;
            }

            Member member = resetToken.getMember();
            member.setPassword(newPassword);

            // 更新密碼
            Member updatedMember = memberService.editMember(member);
            if (updatedMember.isSuccessful()) {
                // 刪除已使用的 token
                verifyDao.deleteById(resetToken.getTokenId());
                core.setSuccessful(true);
                core.setMessage("密碼重置成功");
                return core;
            } else {
                core.setSuccessful(false);
                core.setMessage("密碼重置失敗：" + updatedMember.getMessage());
                return core;
            }

        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("密碼重置失敗：" + e.getMessage());
            return core;
        }
    }
} 