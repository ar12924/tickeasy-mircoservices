package microservices.member.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import microservices.member.service.MemberService;
import microservices.member.service.VerificationService;
import microservices.member.vo.Member;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/member")
public class LegacyMemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/login/{username}/{password}")
    public Core<Member> login(@PathVariable("username") String username,
                              @PathVariable("password") String password,
                              HttpSession session) {
        Member payload = new Member();
        payload.setUserName(username);
        payload.setPassword(password);

        Member result = memberService.login(payload);
        Core<Member> core = new Core<>();
        core.setSuccessful(result.isSuccessful());
        core.setMessage(result.getMessage());
        if (result.isSuccessful()) {
            // 仍維持相容：保留 Session，但雲端建議改 JWT（已提供 auth-service）
            session.setAttribute("member", result);
            core.setData(result);
        }
        return core;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Core<Member> register(@RequestBody Member member) {
        Core<Member> core = new Core<>();
        Member result = memberService.register(member);
        core.setSuccessful(result.isSuccessful());
        core.setMessage(result.getMessage());
        core.setData(result);
        return core;
    }

    @PostMapping(value = "/register/resend-verification", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Core<Member> resendVerification(@RequestParam("email") String email) {
        Member result = memberService.resendVerificationMail(email);
        Core<Member> core = new Core<>();
        core.setSuccessful(result.isSuccessful());
        core.setMessage(result.getMessage());
        core.setData(result);
        return core;
    }

    @GetMapping("/verify")
    public Core<Object> verify(@RequestParam("token") String token) {
        boolean ok = memberService.activateMemberByToken(token);
        Core<Object> core = new Core<>();
        core.setSuccessful(ok);
        core.setMessage(ok ? "帳號啟用成功" : "驗證失敗或連結已失效");
        return core;
    }

    @PostMapping(value = "/reset-password/request", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Core<Member> requestReset(@RequestParam("email") String email) {
        Member member = memberService.requestPasswordResetByEmail(email);
        Core<Member> core = new Core<>();
        core.setSuccessful(member.isSuccessful());
        core.setMessage(member.getMessage());
        core.setData(member);
        return core;
    }

    @GetMapping("/reset-password/verify")
    public Core<Object> verifyReset(@RequestParam("token") String token) {
        return verificationService.verifyResetToken(token);
    }

    @PostMapping(value = "/reset-password/reset", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Core<Member> resetPassword(@RequestParam("token") String token,
                                      @RequestParam("newPassword") String newPassword) {
        Member result = memberService.resetPasswordByToken(token, newPassword);
        Core<Member> core = new Core<>();
        core.setSuccessful(result.isSuccessful());
        core.setMessage(result.getMessage());
        core.setData(result);
        return core;
    }

    // 與舊版 user.member.controller.EditController#getInfo 路由衝突，改用另一條相容查詢路徑
    @GetMapping("/profile")
    public Core<Member> getProfile(@SessionAttribute(name = "member", required = false) Member loginMember) {
        Core<Member> core = new Core<>();
        if (loginMember == null) {
            core.setSuccessful(false);
            core.setMessage("尚未登入");
            return core;
        }
        core.setSuccessful(true);
        core.setData(loginMember);
        return core;
    }

    // 與前端現有 JS 相容：提供 /user/member/edit 同等回傳
    @GetMapping("/edit")
    public Core<Member> getEditInfo(@SessionAttribute(name = "member", required = false) Member loginMember) {
        return getProfile(loginMember);
    }

    // 與舊版 update 路由相同，仍保留供前端使用
    @PostMapping(value = "/edit/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Core<Member> updateProfile(@SessionAttribute(name = "member", required = false) Member loginMember,
                                      @RequestBody Member reqMember) {
        Core<Member> core = new Core<>();
        if (loginMember == null) {
            core.setSuccessful(false);
            core.setMessage("尚未登入");
            return core;
        }
        reqMember.setMemberId(loginMember.getMemberId());
        reqMember.setUserName(loginMember.getUserName());
        Member updated = memberService.editMember(reqMember);
        core.setSuccessful(updated.isSuccessful());
        core.setMessage(updated.getMessage());
        core.setData(updated);
        if (updated.isSuccessful()) {
            // 更新 Session 中的會員資訊
            loginMember.setNickName(updated.getNickName());
            loginMember.setEmail(updated.getEmail());
            loginMember.setPhone(updated.getPhone());
            loginMember.setBirthDate(updated.getBirthDate());
            loginMember.setGender(updated.getGender());
        }
        return core;
    }

    @DeleteMapping("/logout")
    public Core<String> logout(HttpSession session) {
        Core<String> core = new Core<>();
        session.invalidate();
        core.setSuccessful(true);
        core.setMessage("登出成功");
        return core;
    }
}


