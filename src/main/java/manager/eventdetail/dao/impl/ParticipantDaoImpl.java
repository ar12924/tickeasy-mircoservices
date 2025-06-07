package manager.eventdetail.dao.impl;

import manager.eventdetail.dao.ParticipantDao;
import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ParticipantDaoImpl implements ParticipantDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<BuyerTicketEventVer> getParticipantList(Integer eventId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                "LEFT JOIN FETCH bt.buyerOrder bo " +
                "LEFT JOIN FETCH bt.eventTicketType ett " +
                "WHERE ett.eventId = :eventId";
        return session
                .createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("eventId", eventId)
                .list();
    }

    @Override
    public BuyerTicketEventVer getParticipantDetail(Long ticketId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                "LEFT JOIN FETCH bt.buyerOrder " +
                "LEFT JOIN FETCH bt.eventTicketType " +
                "WHERE bt.ticketId = :ticketId";
        return session
                .createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("ticketId", ticketId)
                .uniqueResult();
    }

    @Override
    public BuyerOrderEventVer getOrderInfo(Integer orderId) {
        String hql = "FROM BuyerOrderEventVer bo " +
                "LEFT JOIN FETCH bo.buyerTicketEventVer " +
                "WHERE bo.orderId = :orderId";
        return session
                .createQuery(hql, BuyerOrderEventVer.class)
                .setParameter("orderId", orderId)
                .uniqueResult();
    }

    @Override
    public int countTicketsByOrderId(Integer orderId) {
        String hql = "SELECT COUNT(*) FROM BuyerTicketEventVer WHERE orderId = :orderId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("orderId", orderId)
                .uniqueResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams) {
        Map<String, Object> result = new HashMap<>();
        StringBuilder hql = new StringBuilder();
        hql.append("FROM BuyerTicketEventVer bt ")
                .append("LEFT JOIN FETCH bt.buyerOrder bo ")
                .append("LEFT JOIN FETCH bt.eventTicketType ett ")
                .append("WHERE ett.eventId = :eventId ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("eventId", eventId);

        // 加入查詢條件
        if (searchParams.containsKey("participantName")) {
            hql.append("AND bt.participantName LIKE :participantName ");
            parameters.put("participantName", "%" + searchParams.get("participantName") + "%");
        }
        if (searchParams.containsKey("email")) {
            hql.append("AND bt.email LIKE :email ");
            parameters.put("email", "%" + searchParams.get("email") + "%");
        }
        if (searchParams.containsKey("phone")) {
            hql.append("AND bt.phone LIKE :phone ");
            parameters.put("phone", "%" + searchParams.get("phone") + "%");
        }
        if (searchParams.containsKey("status")) {
            hql.append("AND bt.status = :status ");
            parameters.put("status", searchParams.get("status"));
        }
        if (searchParams.containsKey("ticketTypeId")) {
            hql.append("AND bt.typeId = :typeId ");
            parameters.put("typeId", searchParams.get("ticketTypeId"));
        }
        if (searchParams.containsKey("isUsed")) {
            hql.append("AND bt.isUsed = :isUsed ");
            parameters.put("isUsed", searchParams.get("isUsed"));
        }

        // 分頁處理
        int pageNumber = (int) searchParams.getOrDefault("pageNumber", 1);
        int pageSize = (int) searchParams.getOrDefault("pageSize", 10);
        int firstResult = (pageNumber - 1) * pageSize;

        // 執行查詢
        Query<BuyerTicketEventVer> query = session.createQuery(hql.toString(), BuyerTicketEventVer.class);
        parameters.forEach(query::setParameter);
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);
        List<BuyerTicketEventVer> list = query.list();

        // 獲取總數
        String countHql = "SELECT COUNT(bt.ticketId) "
                + "FROM BuyerTicketEventVer bt "
                + "JOIN bt.eventTicketType ett "
                + "WHERE ett.eventId = :eventId";

        Query<Long> countQuery = session.createQuery(countHql, Long.class);
        parameters.forEach(countQuery::setParameter);
        Long total = countQuery.uniqueResult();

        // 設置分頁
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);
        result.put("total", total);
        result.put("data", list);
        return result;
    }

    @Override
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        String hql = "FROM EventTicketType ett WHERE ett.eventId = :eventId";
        return getSession().createQuery(hql, EventTicketType.class)
                .setParameter("eventId", eventId)
                .list();
    }

    @Override
    public boolean updateTicketStatus(Long ticketId, Integer status, Integer isUsed) {
        String hql = "UPDATE BuyerTicketEventVer SET status = :status, isUsed = :isUsed WHERE ticketId = :ticketId";
        int updated = getSession().createQuery(hql)
                .setParameter("status", status)
                .setParameter("isUsed", isUsed)
                .setParameter("ticketId", ticketId)
                .executeUpdate();
        return updated > 0;
    }
}