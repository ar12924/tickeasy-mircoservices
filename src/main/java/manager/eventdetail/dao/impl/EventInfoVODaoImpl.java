package manager.eventdetail.dao.impl;

import manager.eventdetail.dao.EventInfoVODao;
import manager.eventdetail.vo.EventInfoEventVer;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;

@Repository
public class EventInfoVODaoImpl implements EventInfoVODao {
    @PersistenceContext
    private Session session;

    @Override
    public EventInfoEventVer getEventById(Integer eventId) {
        String hql = "SELECT new manager.eventdetail.vo.EventInfoVO(e.eventId, e.eventName, e.memberId, e.eventFromDate, e.eventToDate) FROM EventInfo e WHERE e.eventId = :eventId";
        return session.createQuery(hql, EventInfoEventVer.class)
                .setParameter("eventId", eventId)
                .uniqueResult();
    }
} 