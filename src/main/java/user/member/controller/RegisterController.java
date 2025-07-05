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
            @RequestParam("userName") String userName,
            @RequestParam("nickName") String nickName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("rePassword") String rePassword,
            @RequestParam("birthDate") String birthDate,
            @RequestParam("phone") String phone,
            @RequestParam("gender") String gender,
            @RequestParam("idCard") String idCard,
            @RequestParam(value = "unicode", required = false) String unicode,
            @RequestParam("agree") String agree,
            @RequestParam(value = "hostApply", required = false) String hostApply,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {

        Core<Member> core = new Core<>();
        Date sqlBirthDate = null;
        try {
            LocalDate localDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            sqlBirthDate = Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            core.setSuccessful(false);
            core.setMessage("出生日期格式錯誤，請使用 YYYY-MM-DD 格式");
            return core;
        }

        Member member = new Member();
        member.setUserName(userName);
        member.setNickName(nickName);
        member.setEmail(email);
        member.setPassword(password);
        member.setRePassword(rePassword);
        member.setBirthDate(sqlBirthDate);
        member.setPhone(phone);
        member.setGender(gender);
        member.setIdCard(idCard);
        if (unicode != null && !unicode.trim().isEmpty()) {
            member.setUnicode(unicode);
        }
        member.setAgree("true".equals(agree));
        member.setHostApply(hostApply != null && "true".equals(hostApply));

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

    @PostMapping("resend-verification")
    public Core<Void> resendVerification(@RequestParam String email) {
        Core<Void> core = new Core<>();

        try {
            Member member = service.getByEmail(email);
            if (member == null) {
                core.setSuccessful(false);
                core.setMessage("找不到此 email 對應的會員帳號");
                return core;
            }

            if (member.getRoleLevel() > 0) {
                core.setSuccessful(false);
                core.setMessage("此帳號已經驗證過了");
                return core;
            }

            String tokenName = UUID.randomUUID().toString();
            VerificationToken token = new VerificationToken();
            token.setTokenName(tokenName);
            token.setTokenType("EMAIL_VERIFY");
            token.setExpiredTime(new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000)); // 24小時
            token.setMember(member);

            service.getVerificationDao().insert(token);

            service.getMailService().sendActivationNotification(member.getEmail(), member.getUserName(), tokenName);

            core.setSuccessful(true);
            core.setMessage("驗證信已重新發送，請檢查您的信箱");

        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("重新發送驗證信失敗：" + e.getMessage());
        }

        return core;
    }
}
