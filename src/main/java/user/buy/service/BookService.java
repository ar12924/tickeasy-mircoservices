package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.Attendee;
import user.buy.vo.BookDto;
import user.buy.vo.BookTypeDto;
import user.buy.vo.EventInfo;
import user.member.vo.Member;

public interface BookService {

	public List<BookTypeDto> getTypeById(Integer eventId);

	public EventInfo getEventById(Integer eventId);

	public Core<String> saveBookType(BookDto book, long timeoutMinutes);

	public Core<String> saveBookInfo(BookDto book);

	public Core<BookDto> getBook(String userName);

	public Member getMember(String userName);

	public Core<String> verifyMemberIdCard(Attendee reqAttendee);
	
	public Core<String> saveBookConfirm(BookDto book);
	
	public Core<String> saveOrderAndTicket(BookDto book);
}
