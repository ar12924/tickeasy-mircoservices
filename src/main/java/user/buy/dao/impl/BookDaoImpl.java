package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;
import user.buy.vo.BookTypeInfoDto;

@Repository
public class BookDaoImpl implements BookDao {

	@PersistenceContext
	private Session session;

	@Override
	public List<BookTypeInfoDto> selectTypeJoinEventById(Integer eventId) {
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookTypeInfoDto(");
		hqlTemp.append("ett.typeId, ett.categoryName, ett.price, ett.capacity, ");
		hqlTemp.append("ei.eventId, ei.eventName, ei.isPosted) ");
		hqlTemp.append("FROM EventTicketType ett ");
		hqlTemp.append("JOIN EventInfo ei ON ett.eventId = ei.eventId ");
		hqlTemp.append("WHERE ett.eventId = :eventId");
		var hql = hqlTemp.toString();

		// 2. 透過 eventId 查詢 type + event
		Query<BookTypeInfoDto> query = session.createQuery(hql, BookTypeInfoDto.class);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

}
