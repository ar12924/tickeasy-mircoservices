package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;

@Repository
public class BookDaoImpl implements BookDao {
	@PersistenceContext
	private Session session;

	@Override
	public List<Object[]> selectTypeJoinEventById(int eventId) {
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("FROM EventTicketType ett ");
		hqlTemp.append("JOIN EventInfo ei ON ett.eventId = ei.eventId ");
		hqlTemp.append("WHERE ett.eventId = ");
		hqlTemp.append(eventId);
		var hql = hqlTemp.toString();
		// 2. 透過 eventId 查詢 type + event
		Query<Object[]> query = session.createQuery(hql, Object[].class);
		List<Object[]> typeAndEventList = query.getResultList();
		return typeAndEventList;
	}

}
