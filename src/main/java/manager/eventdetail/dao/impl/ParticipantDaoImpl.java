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
import javax.persistence.criteria.*;

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
    public BuyerTicketEventVer getParticipantDetail(Integer ticketId) {
        String hql = "FROM BuyerTicketEventVer bt " +
                     "LEFT JOIN FETCH bt.buyerOrder " +
                     "LEFT JOIN FETCH bt.eventTicketType " +
                     "WHERE bt.ticketId = :ticketId";
        return session.createQuery(hql, BuyerTicketEventVer.class)
                .setParameter("ticketId", ticketId)
                .uniqueResult();
    }

    @Override
    public BuyerOrderEventVer getOrderInfo(Integer orderId) {
        String hql = "FROM BuyerOrderEventVer bo WHERE bo.orderId = :orderId";
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

        // 步驟 1: 根據 eventId 獲取 eventName
        String eventName = getEventNameById(eventId);
        if (eventName == null) {
            System.out.println("DAO: 找不到 eventId " + eventId + " 對應的 eventName。");
            result.put("total", 0L);
            result.put("participants", new java.util.ArrayList<>());
            return result;
        }

        // --- Criteria API 查詢 ---
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<BuyerTicketEventVer> cq = cb.createQuery(BuyerTicketEventVer.class);
        Root<BuyerTicketEventVer> root = cq.from(BuyerTicketEventVer.class);
        Join<Object, Object> ticketTypeJoin = root.join("eventTicketType", JoinType.LEFT);
        root.fetch("buyerOrder", JoinType.LEFT);
        root.fetch("eventTicketType", JoinType.LEFT);

        List<Predicate> predicates = new java.util.ArrayList<>();
        predicates.add(cb.equal(root.get("eventName"), eventName));

        if (searchParams.containsKey("participantName") && searchParams.get("participantName") != null && !searchParams.get("participantName").toString().isEmpty()) {
            predicates.add(cb.like(root.get("participantName"), "%" + searchParams.get("participantName") + "%"));
        }
        if (searchParams.containsKey("email") && searchParams.get("email") != null && !searchParams.get("email").toString().isEmpty()) {
            predicates.add(cb.like(root.get("email"), "%" + searchParams.get("email") + "%"));
        }
        if (searchParams.containsKey("phone") && searchParams.get("phone") != null && !searchParams.get("phone").toString().isEmpty()) {
            predicates.add(cb.like(root.get("phone"), "%" + searchParams.get("phone") + "%"));
        }
        if (searchParams.containsKey("status") && searchParams.get("status") != null) {
            predicates.add(cb.equal(root.get("status"), searchParams.get("status")));
        }
        if (searchParams.containsKey("ticketTypeId") && searchParams.get("ticketTypeId") != null) {
            predicates.add(cb.equal(ticketTypeJoin.get("typeId"), searchParams.get("ticketTypeId")));
        }
        if (searchParams.containsKey("isUsed") && searchParams.get("isUsed") != null) {
            predicates.add(cb.equal(root.get("isUsed"), searchParams.get("isUsed")));
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));

        // 分頁查詢
        int pageNumber = (int) searchParams.getOrDefault("pageNumber", 1);
        int pageSize = (int) searchParams.getOrDefault("pageSize", 10);
        int firstResult = (pageNumber - 1) * pageSize;
        List<BuyerTicketEventVer> list = session.createQuery(cq)
                .setFirstResult(firstResult)
                .setMaxResults(pageSize)
                .getResultList();

        // --- 總數查詢 ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<BuyerTicketEventVer> countRoot = countQuery.from(BuyerTicketEventVer.class);
        Join<Object, Object> countTicketTypeJoin = countRoot.join("eventTicketType", JoinType.LEFT);
        List<Predicate> countPredicates = new java.util.ArrayList<>();
        countPredicates.add(cb.equal(countRoot.get("eventName"), eventName));
        if (searchParams.containsKey("participantName") && searchParams.get("participantName") != null && !searchParams.get("participantName").toString().isEmpty()) {
            countPredicates.add(cb.like(countRoot.get("participantName"), "%" + searchParams.get("participantName") + "%"));
        }
        if (searchParams.containsKey("email") && searchParams.get("email") != null && !searchParams.get("email").toString().isEmpty()) {
            countPredicates.add(cb.like(countRoot.get("email"), "%" + searchParams.get("email") + "%"));
        }
        if (searchParams.containsKey("phone") && searchParams.get("phone") != null && !searchParams.get("phone").toString().isEmpty()) {
            countPredicates.add(cb.like(countRoot.get("phone"), "%" + searchParams.get("phone") + "%"));
        }
        if (searchParams.containsKey("status") && searchParams.get("status") != null) {
            countPredicates.add(cb.equal(countRoot.get("status"), searchParams.get("status")));
        }
        if (searchParams.containsKey("ticketTypeId") && searchParams.get("ticketTypeId") != null) {
            countPredicates.add(cb.equal(countTicketTypeJoin.get("typeId"), searchParams.get("ticketTypeId")));
        }
        if (searchParams.containsKey("isUsed") && searchParams.get("isUsed") != null) {
            countPredicates.add(cb.equal(countRoot.get("isUsed"), searchParams.get("isUsed")));
        }
        countQuery.select(cb.count(countRoot.get("ticketId"))).where(countPredicates.toArray(new Predicate[0]));
        Long total = session.createQuery(countQuery).getSingleResult();

        result.put("total", total);
        result.put("participants", list);
        return result;
    }

    @Override
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        String hql = "FROM EventTicketType ett WHERE ett.eventId = :eventId";
        return session.createQuery(hql, EventTicketType.class)
                .setParameter("eventId", eventId)
                .list();
    }

    @Override
    public boolean updateTicketStatus(Integer ticketId, Integer status, Integer isUsed) {
        String hql = "UPDATE BuyerTicketEventVer SET status = :status, isUsed = :isUsed WHERE ticketId = :ticketId";
        int updated = session.createQuery(hql)
                .setParameter("status", status)
                .setParameter("isUsed", isUsed)
                .setParameter("ticketId", ticketId)
                .executeUpdate();
        return updated > 0;
    }

    @Override
    public String getEventNameById(Integer eventId) {
        String hql = "SELECT e.eventName FROM EventInfoEventVer e WHERE e.eventId = :eventId";
        return session.createQuery(hql, String.class)
                .setParameter("eventId", eventId)
                .uniqueResult();
    }

    @Override
    public Map<String, Object> getParticipants(Integer eventId, int page, int pageSize) {
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("pageNumber", page);
        searchParams.put("pageSize", pageSize);
        return searchParticipants(eventId, searchParams);
    }
}