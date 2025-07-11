package user.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import user.member.service.MemberService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("user/member/verify")
public class VerifyController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public String verify(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        boolean result = memberService.activateMemberByToken(token);
        if (result) {
            // 驗證成功，導向登入畫面
            return "redirect:/user/member/login.html?verified=true";
        } else {
            // 驗證失敗，導向登入畫面並帶入失敗訊息
            return "redirect:/user/member/login.html?verified=false";
        }
    }
}
