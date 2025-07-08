package user.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import common.vo.Core;
import user.member.vo.Member;
import user.member.service.MemberService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.sql.Timestamp;

import user.member.vo.VerificationToken;

@RestController
@RequestMapping("user/member/register")
public class RegisterController {

    @Autowired
    private MemberService service;

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public Core<Member> register(
            @RequestParam String userName,
            @RequestParam String nickName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String rePassword,
            @RequestParam String birthDate,
            @RequestParam String phone,
            @RequestParam String gender,
            @RequestParam String idCard,
            @RequestParam(required = false) String unicode,
            @RequestParam(defaultValue = "false") Boolean agree,
            @RequestParam(defaultValue = "false") Boolean hostApply,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        Core<Member> core = new Core<>();
        
        try {
            Member member = new Member();
            member.setUserName(userName);
            member.setNickName(nickName);
            member.setEmail(email);
            member.setPassword(password);
            member.setRePassword(rePassword);
            member.setBirthDate(Date.valueOf(birthDate));
            member.setPhone(phone);
            member.setGender(gender);
            member.setIdCard(idCard);
            member.setUnicode(unicode);
            member.setAgree(agree);
            member.setHostApply(hostApply != null && hostApply);
            
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
