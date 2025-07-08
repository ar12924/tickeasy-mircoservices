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
            // 手動驗證
            if (userName == null || userName.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("使用者名稱不可為空");
                return core;
            }
            
            if (nickName == null || nickName.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("暱稱不可為空");
                return core;
            }
            
            if (email == null || email.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("Email 不可為空");
                return core;
            }
            
            if (password == null || password.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("密碼不可為空");
                return core;
            }
            
            if (rePassword == null || rePassword.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("請再次輸入密碼");
                return core;
            }
            
            if (birthDate == null || birthDate.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("出生日期不可為空");
                return core;
            }
            
            if (phone == null || phone.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("手機號碼不可為空");
                return core;
            }
            
            if (gender == null || gender.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("性別不可為空");
                return core;
            }
            
            if (idCard == null || idCard.trim().isEmpty()) {
                core.setSuccessful(false);
                core.setMessage("身分證不可為空");
                return core;
            }
            
            if (agree == null || !agree) {
                core.setSuccessful(false);
                core.setMessage("請同意服務條款");
                return core;
            }
            
            // 創建 Member 物件
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
