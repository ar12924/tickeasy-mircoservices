package user.buy.dao;

import user.buy.vo.Member;

public interface MemberDao {
	Member selectMemberById(int id);
}
