package microservices.member.service;

import common.vo.Core;

public interface VerificationService {
    Core<Object> verifyResetToken(String token);
}


