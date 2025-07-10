package user.member.controller;

import common.vo.Core;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;

@RestController
@RequestMapping("user/member/logout")
public class LogoutController {

    @DeleteMapping
    public Core<Void> logout(SessionStatus sessionStatus) {
        Core<Void> core = new Core<>();
        sessionStatus.setComplete();
        core.setSuccessful(true);
        core.setMessage("登出成功");
        return core;
    }
}
