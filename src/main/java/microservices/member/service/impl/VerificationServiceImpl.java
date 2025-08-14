package microservices.member.service.impl;

import common.vo.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservices.member.dao.VerificationDao;
import microservices.member.service.VerificationService;
import microservices.member.vo.VerificationToken;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationDao verificationDao;

    @Autowired
    public VerificationServiceImpl(VerificationDao verificationDao) {
        this.verificationDao = verificationDao;
    }

    @Override
    public Core<Object> verifyResetToken(String token) {
        var core = new Core<>();
        VerificationToken t = verificationDao.findByToken(token);
        boolean ok = t != null;
        core.setSuccessful(ok);
        core.setMessage(ok ? "驗證成功" : "連結無效或已過期");
        return core;
    }
}


