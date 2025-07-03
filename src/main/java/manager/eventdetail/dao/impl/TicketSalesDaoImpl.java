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
    public Integer getUsedTicketCount(Integer typeId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE typeId = :typeId AND isUsed = 1";
        Object result = session.createQuery(hql)
                .setParameter("typeId", typeId)
                .uniqueResult();
        return result != null ? ((Long) result).intValue() : 0;
    }

    @Override
    public Map<Integer, Map<String, Object>> getTicketSalesStatistics(Integer eventId) {
        Map<Integer, Map<String, Object>> result = new HashMap<>();
        // 使用 Criteria API 一次查詢所有票種的銷售統計
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<EventTicketType> ticketTypeRoot = cq.from(EventTicketType.class);
        Join<Object, Object> ticketJoin = ticketTypeRoot.join("buyerTicketEventVer", JoinType.LEFT);

        cq.multiselect(
                ticketTypeRoot.get("typeId"),
                ticketTypeRoot.get("categoryName"),
                ticketTypeRoot.get("capacity"),
                cb.count(ticketJoin.get("ticketId")),
                cb.sum(cb.<Integer>selectCase().when(cb.equal(ticketJoin.get("isUsed"), 1), 1).otherwise(0))
        );
        cq.where(cb.equal(ticketTypeRoot.get("eventId"), eventId));
        cq.groupBy(ticketTypeRoot.get("typeId"), ticketTypeRoot.get("categoryName"), ticketTypeRoot.get("capacity"));

        List<Object[]> queryResult = session.createQuery(cq).getResultList();

        for (Object[] row : queryResult) {
            Integer typeId = (Integer) row[0];
            String categoryName = (String) row[1];
            Integer capacity = (Integer) row[2];
            Long soldCount = (Long) row[3];
            Long usedCount = row[4] != null ? ((Number) row[4]).longValue() : 0L;

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("typeId", typeId);
            statistics.put("categoryName", categoryName);
            statistics.put("capacity", capacity);
            statistics.put("soldCount", soldCount);
            statistics.put("usedCount", usedCount);
            statistics.put("remainingCount", capacity != null ? capacity - soldCount.intValue() : 0);
            statistics.put("soldPercentage", (capacity != null && capacity > 0) ? (double) soldCount / capacity * 100 : 0);

            result.put(typeId, statistics);
        }
        return result;
    }
    
    @Override
    public Map<String, Object> getSalesDashboardData(Integer eventId) {
        String salesHql = "SELECT " +
                "ett.categoryName, " +
                "COUNT(bt.ticketId), " +
                "COALESCE(SUM(ett.price), 0.0) " +
                "FROM BuyerTicketEventVer bt " +
                "JOIN bt.eventTicketType ett " +
                "WHERE ett.eventId = :eventId " +
                "GROUP BY ett.categoryName";

        List<Object[]> queryResult = session.createQuery(salesHql, Object[].class)
                .setParameter("eventId", eventId)
                .list();

        List<Map<String, Object>> salesData = new ArrayList<>();
        long totalTickets = 0L;
        double totalRevenue = 0.0;

        if (queryResult != null) {
            for (Object[] row : queryResult) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("categoryName", row[0]);
                stat.put("ticketsSold", row[1]);
                stat.put("totalRevenue", row[2]);
                salesData.add(stat);

                if (row[1] != null && row[1] instanceof Number) {
                    totalTickets += ((Number) row[1]).longValue();
                }
                if (row[2] != null && row[2] instanceof Number) {
                    totalRevenue += ((Number) row[2]).doubleValue();
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("salesData", salesData);
        result.put("totalTickets", totalTickets);
        result.put("totalRevenue", totalRevenue);

        return result;
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
