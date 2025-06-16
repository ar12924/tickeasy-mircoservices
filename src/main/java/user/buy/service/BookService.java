package user.buy.service;

import java.util.List;

import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.buy.vo.BookDto;

public interface BookService {
	
	public List<BookTypeDto> getTypeById(Integer eventId);
	
	public BookEventDto getEventById(Integer eventId);
    
	public void saveBook(BookDto book, long timeoutMinutes);
    
    public BookDto getBook(String userName);
}
