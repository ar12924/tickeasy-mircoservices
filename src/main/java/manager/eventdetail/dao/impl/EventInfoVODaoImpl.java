package manager.eventdetail.dao.impl;

import java.util.List;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import manager.eventdetail.dao.EventInfoVODao;
import manager.eventdetail.vo.EventInfoEventVer;

@Repository
public class EventInfoVODaoImpl implements EventInfoVODao {

    @PersistenceContext
    private Session session;

    @Override
    public EventInfoEventVer getEventById(Integer eventId) {
        return session.get(EventInfoEventVer.class, eventId);
    }

    @Override
    public List<EventInfoEventVer> getEventsByMemberId(Integer memberId) {
        String hql = "FROM EventInfoEventVer e WHERE e.memberId = :memberId ORDER BY e.eventFromDate DESC";
        return session.createQuery(hql, EventInfoEventVer.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
} 