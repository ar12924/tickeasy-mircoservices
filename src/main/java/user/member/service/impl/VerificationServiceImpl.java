package user.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.member.dao.VerificationDao;
import user.member.vo.VerificationToken;
import user.member.vo.Member;
import common.vo.Core;
import user.member.service.VerificationService;

import java.sql.Timestamp;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationDao verifyDao;

    @Override
    public Core<Object> verifyResetToken(String token) {
        Core<Object> core = new Core<>();
        try {
            VerificationToken resetToken = verifyDao.findByToken(token);
            if (resetToken == null ||
                    resetToken.getExpiredTime().before(new Timestamp(System.currentTimeMillis())) ||
                    !"RESET_PASSWORD".equals(resetToken.getTokenType())) {
                core.setSuccessful(false);
                core.setMessage("無效或已過期的重置連結");
                return core;
            }
            Member member = resetToken.getMember();
            Member safeMember = new Member();
            safeMember.setMemberId(member.getMemberId());
            safeMember.setUserName(member.getUserName());
            safeMember.setNickName(member.getNickName());
            safeMember.setEmail(member.getEmail());

            core.setSuccessful(true);
            core.setMessage("Token 驗證成功");
            core.setData(safeMember);
            return core;
        } catch (Exception e) {
            core.setSuccessful(false);
            core.setMessage("驗證失敗：" + e.getMessage());
            return core;
        }
    }
} 