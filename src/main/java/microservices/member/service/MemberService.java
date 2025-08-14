package microservices.member.service;

import common.service.CommonService;
import microservices.member.vo.Member;

public interface MemberService extends CommonService {
    Member register(Member member);

    Member editMember(Member member);

    Member login(Member member);

    Member getById(Integer memberId, Member loginMember);

    Member getByUsername(String username);

    Member getByEmail(String email);

    String getRoleById(Integer memberId);

    boolean removeMemberById(Integer memberId);

    boolean activateMemberByToken(String tokenStr);

    Member requestPasswordResetByEmail(String email);

    Member sendPasswordUpdateMail(Member member, String newPassword);

    Member resetPasswordByToken(String token, String newPassword);

    Member resendVerificationMail(String email);

    Member sendVerificationMail(Member member);

    Member updatePasswordAndDeleteToken(Member member, String newPassword, Integer tokenId);
}


