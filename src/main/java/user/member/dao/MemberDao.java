package user.member.dao;

import java.util.List;

import user.member.vo.Member;

public interface MemberDao {
	  boolean insert(Member member);
	  boolean update(Member member);
	  Member findByUserName(String userName);
	  Member findById(int memberId);            
	  boolean delete(int memberId);             
	  List<Member> listAll();   
}
