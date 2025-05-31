package user.buy.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.buy.dao.TypeDao;
import user.buy.service.TypeService;
import user.buy.vo.TicketType;

@Service
public class TypeServiceImpl implements TypeService{
	@Autowired
	private TypeDao dao;

	@Override
	public List<TicketType> findTicketType(Integer eventId) {
		return dao.selectById(eventId);
	}
	
	

}
