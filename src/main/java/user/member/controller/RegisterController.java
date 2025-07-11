package user.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import user.member.service.MemberService;
import user.member.vo.Member;

import java.sql.Date;

@RestController
@RequestMapping("user/member/register")
public class RegisterController {

    @Autowired
    private MemberService service;

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Core<Member> register(@RequestParam("member") String memberJson, @RequestPart(value = "photo", required = false) MultipartFile photo) {
        Core<Member> core = new Core<>();

        try {
            // 使用 ObjectMapper 解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            Member member = mapper.readValue(memberJson, Member.class);

            if (photo != null && !photo.isEmpty()) {
                member.setPhoto(photo.getBytes());
            }

            Member result = service.register(member);
            if (result.isSuccessful()) {
                core.setSuccessful(true);
                core.setMessage("註冊成功");
                core.setData(result);
            } else {
                core.setSuccessful(false);
                core.setMessage(result.getMessage());
            }
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("註冊失敗：" + e.getMessage());
        }
        return core;
    }

    @PostMapping("resend-verification")
    public Core<Member> resendVerification(@RequestParam String email) {
        Core<Member> core = new Core<>();
        Member member = service.resendVerificationMail(email);
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
