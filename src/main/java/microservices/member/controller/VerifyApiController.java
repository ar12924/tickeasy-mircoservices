package microservices.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import microservices.member.service.MemberService;

@RestController
@RequestMapping("/api/members/verify")
public class VerifyApiController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        boolean ok = memberService.activateMemberByToken(token);
        if (ok) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("驗證失敗或連結已失效");
    }
}


