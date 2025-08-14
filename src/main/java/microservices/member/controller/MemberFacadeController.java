package microservices.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import microservices.member.service.MemberService;
import microservices.member.vo.Member;

@RestController
@RequestMapping("/api/members")
public class MemberFacadeController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Member> login(@RequestBody Member payload) {
        Member result = memberService.login(payload);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody Member payload) {
        Member result = memberService.register(payload);
        return ResponseEntity.ok(result);
    }
}


