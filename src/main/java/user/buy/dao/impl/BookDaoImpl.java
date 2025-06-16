package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;

@Repository
public class BookDaoImpl implements BookDao {

	@PersistenceContext
	private Session session;

	@Override
	public List<BookTypeDto> selectTypeById(Integer eventId) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookTypeDto(");
		hqlTemp.append("typeId, categoryName, price, capacity) ");
		hqlTemp.append("FROM EventTicketType ");
		hqlTemp.append("WHERE eventId = :eventId");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<BookTypeDto> query = session.createQuery(hql, BookTypeDto.class);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}
	
	@Override
	public BookEventDto selectEventById(Integer eventId) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookEventDto(");
		hqlTemp.append("eventId, eventName, isPosted) ");
		hqlTemp.append("FROM EventInfo ");
		hqlTemp.append("WHERE eventId = :eventId");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<BookEventDto> query = session.createQuery(hql, BookEventDto.class);
		query.setParameter("eventId", eventId);
		return query.uniqueResult();
	}

}
