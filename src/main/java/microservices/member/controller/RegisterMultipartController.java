package microservices.member.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import microservices.member.service.S3PhotoService;
import microservices.member.service.MemberService;
import microservices.member.vo.Member;

@RestController
@RequestMapping("/api/members")
public class RegisterMultipartController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private S3PhotoService s3PhotoService;

    @PostMapping(value = "/register")
    public Core<Member> registerMultipart(@RequestBody Member member) {
        Core<Member> core = new Core<>();
        try {
            Member result = memberService.register(member);
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
}


