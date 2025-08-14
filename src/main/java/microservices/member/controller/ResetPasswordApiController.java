package microservices.member.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import microservices.member.service.MemberService;
import microservices.member.service.VerificationService;
import microservices.member.vo.Member;

@RestController
@RequestMapping("/api/members/reset-password")
public class ResetPasswordApiController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/request")
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

    @GetMapping("/verify")
    public ResponseEntity<Core<Object>> verifyResetToken(@RequestParam String token) {
        Core<Object> core = verificationService.verifyResetToken(token);
        if (core.isSuccessful()) {
            return ResponseEntity.ok(core);
        }
        return ResponseEntity.badRequest().body(core);
    }

    @PostMapping("/reset")
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


