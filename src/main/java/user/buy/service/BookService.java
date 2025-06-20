package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;
import user.buy.vo.BookDto;

public interface BookService {
	
	public List<BookTypeDto> getTypeById(Integer eventId);
	
	public BookEventDto getEventById(Integer eventId);
    
	public void saveBook(BookDto book, long timeoutMinutes);
    
    public BookDto getBook(String userName);
    
    public Member getMember(String userName);
    
    public Core<Member> verifyMemberByUserName(String userName);
    
    public Core<Member> verifyMemberByIdCard(String idCard);
}
