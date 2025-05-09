package user.member.service;

import java.util.List;

import user.member.vo.Member;

public interface MemberService {
    Member register(Member member);
    Member editMember(Member member);
    Member login(Member member);
    Member getById(Integer memberId, Member loginMember);
    Member getByUsername(String username);
    List<Member> getAll();
    String getRoleById(Integer memberId);
	boolean removeMemberById(Integer memberId);
}
