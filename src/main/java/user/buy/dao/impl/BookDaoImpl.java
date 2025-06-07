package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;
import user.buy.vo.TicketType;

@Repository
public class BookDaoImpl implements BookDao {
	@PersistenceContext
	private Session session;
	
	@Override
	public List<TicketType> selectById(Integer eventId) {
		// 1. HQL 語句
		String hql = "FROM TicketType WHERE eventId = :eventId";
		// 2. 查詢所有
		return session
				.createQuery(hql, TicketType.class)
				.setParameter("eventId", eventId)
				.getResultList();
	}
	
}
