package microservices.member.dao;

import common.dao.CommonDao;
import microservices.member.vo.Member;

public interface MemberDao extends CommonDao {
    boolean insert(Member member);

    boolean update(Member member);

    boolean delete(int memberId);

    Member findByUserName(String userName);

    Member findById(int memberId);

    Member findByEmail(String email);

    Member findByPhone(String phone);
}


