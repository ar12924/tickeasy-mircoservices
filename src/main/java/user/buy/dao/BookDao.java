package user.buy.dao;

import java.util.List;

import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;

public interface BookDao {
	
	List<BookTypeDto> selectTypeById(Integer eventId);
	
	BookEventDto selectEventById(Integer eventId);
	
	public Member selectMemberByUserName(String userName);
	
	public Member selectMemberByIdCard(String idCard);
}
