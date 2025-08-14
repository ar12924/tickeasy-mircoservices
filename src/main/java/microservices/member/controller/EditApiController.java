package microservices.member.controller;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import microservices.member.service.S3PhotoService;
import microservices.member.service.MemberService;
import microservices.member.vo.Member;

@RestController
@RequestMapping("/api/members")
public class EditApiController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private S3PhotoService s3PhotoService; // 暫留，如需生成讀取URL

    @PostMapping(value = "/edit")
    public Core<Member> edit(@RequestBody Member req) {
        Core<Member> core = new Core<>();
        try {
            Member updated = memberService.editMember(req);
            if (updated.isSuccessful()) {
                core.setSuccessful(true);
                core.setMessage("會員資料已更新");
                core.setData(updated);
            } else {
                core.setSuccessful(false);
                core.setMessage(updated.getMessage());
            }
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("會員資料更新失敗：" + e.getMessage());
        }
        return core;
    }
}



