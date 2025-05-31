package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.buy.dao.BookDao;
import user.buy.service.BookService;
import user.buy.vo.TicketType;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	private BookDao dao;

	@Override
	public List<TicketType> findTicketType(Integer eventId) {
		return dao.selectById(eventId);
	}
	
	

}
