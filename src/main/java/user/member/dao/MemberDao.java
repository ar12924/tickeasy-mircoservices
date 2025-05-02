package user.member.dao;

import user.member.vo.Member;

public interface MemberDao {
	Member selectMemberById(Integer id);
}
