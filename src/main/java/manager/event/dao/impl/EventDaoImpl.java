package manager.event.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.event.dao.EventDao;
import manager.event.vo.MngEventInfo;

@Repository
public class EventDaoImpl implements EventDao {
	@PersistenceContext
	private Session session;

	@Override
	public int createEvent(MngEventInfo eventInfo) {
		session.persist(eventInfo);
		return 1;
	}

	@Override
	public MngEventInfo findById(Integer eventId) {
		return session.get(MngEventInfo.class, eventId);
	}

	@Override
	public List<MngEventInfo> findAll() {
		String hql = "FROM MngEventInfo ORDER BY event_id DESC";
		return session.createQuery(hql, manager.event.vo.MngEventInfo.class).getResultList();
	}
}