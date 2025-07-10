package user.member.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.service.VerificationService;
import user.member.vo.Member;

@RestController
@RequestMapping("user/member/reset-password")
public class ResetPasswordController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationService verificationService;

    // 請求密碼重設
    @PostMapping("request")
    public Core<Member> requestPasswordReset(@RequestParam String email) {
        Member member = memberService.requestPasswordResetByEmail(email);
        Core<Member> core = new Core<>();
        if (member.isSuccessful()) {
            core.setSuccessful(true);
            core.setMessage(member.getMessage());
            core.setData(member);
        } else {
            core.setSuccessful(false);
            core.setMessage(member.getMessage());
        }
        return core;
    }

    // 驗證重置 token
    @GetMapping("verify")
    public ResponseEntity<Core<Object>> verifyResetToken(@RequestParam String token) {
        Core<Object> core = verificationService.verifyResetToken(token);
        if (core.isSuccessful()) {
            return ResponseEntity.ok(core);
        } else {
            return ResponseEntity.badRequest().body(core);
        }
    }

    // 執行密碼重設
    @PostMapping("reset")
    public Core<Member> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Member member = memberService.resetPasswordByToken(token, newPassword);
        Core<Member> core = new Core<>();
        if (member.isSuccessful()) {
            core.setSuccessful(true);
            core.setMessage(member.getMessage());
            core.setData(member);
        } else {
            core.setSuccessful(false);
            core.setMessage(member.getMessage());
        }
        return core;
    }
} 