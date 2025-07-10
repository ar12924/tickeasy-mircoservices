package manager.eventdetail.dao.impl;

import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.vo.EventTicketType;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<Map<String, Object>> getSalesTrend(Integer eventId) {
        String hql = "SELECT bo.orderTime, COUNT(bt) " +
                "FROM BuyerTicketEventVer bt " +
                "JOIN bt.buyerOrder bo " +
                "WHERE bt.eventTicketType.eventId = :eventId " +
                "GROUP BY bo.orderTime ORDER BY bo.orderTime";
        List<Object[]> rows = session.createQuery(hql)
                .setParameter("eventId", eventId)
                .getResultList();

        List<Map<String, Object>> trendList = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            java.sql.Timestamp ts = (java.sql.Timestamp) row[0];
            String dateStr = ts.toLocalDateTime().toLocalDate().toString();
            map.put("date", dateStr);
            map.put("value", ((Long) row[1]).intValue());
            trendList.add(map);
        }
        return trendList;
    }

    @Override
    public List<Object[]> findTicketTypeTrendByEventId(Integer eventId) {
        String hql = "SELECT DATE(bo.orderTime), bt.eventTicketType.categoryName, COUNT(bt) " +
                "FROM BuyerTicketEventVer bt " +
                "JOIN bt.buyerOrder bo " +
                "WHERE bt.eventTicketType.eventId = :eventId " +
                "GROUP BY DATE(bo.orderTime), bt.eventTicketType.categoryName " +
                "ORDER BY DATE(bo.orderTime)";
        return session.createQuery(hql)
                .setParameter("eventId", eventId)
                .getResultList();
    }
}
