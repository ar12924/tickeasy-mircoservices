package user.member.service;

import common.vo.Core;
 
public interface VerificationService {
    Core<Object> verifyResetToken(String token);
} 