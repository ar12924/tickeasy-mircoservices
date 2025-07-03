package user.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import common.vo.Core;
import user.member.vo.Member;
import user.member.service.MemberService;

@RestController
@RequestMapping("user/member/register")
public class RegisterController {

    @Autowired
	private MemberService service;

    @PostMapping(value = "register", consumes = {"multipart/form-data"})
    public Core<Member> register(
            @RequestPart("member") Member member,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        Core<Member> core = new Core<>();
        if (member == null) {
            core.setMessage("無會員資訊");
            core.setSuccessful(false);
            return core;
        }
        try {
            if (photo != null && !photo.isEmpty()) {
                member.setPhoto(photo.getBytes());
            }
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("照片上傳失敗");
            return core;
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
        return core;
    }
}
