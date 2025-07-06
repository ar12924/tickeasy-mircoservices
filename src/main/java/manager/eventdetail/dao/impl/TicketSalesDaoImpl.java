package manager.eventdetail.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.vo.EventTicketType;

import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

@Repository
public class TicketSalesDaoImpl implements TicketSalesDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        String hql = "FROM EventTicketType WHERE eventId = :eventId ORDER BY typeId";
        return session.createQuery(hql, EventTicketType.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    @Override
    public Integer getSoldTicketCount(Integer typeId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE typeId = :typeId";
        Object result = session.createQuery(hql)
                .setParameter("typeId", typeId)
                .uniqueResult();
        return result != null ? ((Long) result).intValue() : 0;
    }

    @Override
    public Integer getSoldTicketCountByEventId(Integer eventId) {
        String hql = "SELECT COUNT(bt) FROM BuyerTicketEventVer bt WHERE bt.eventTicketType.eventId = :eventId";
        Object result = session.createQuery(hql)
                .setParameter("eventId", eventId)
                .uniqueResult();
        return result != null ? ((Long) result).intValue() : 0;
    }
}
