package user.buy.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.buy.dao.EventInfoDAO;
import user.buy.vo.EventBuyVO;
import user.buy.vo.FavoriteVO;
import user.buy.vo.TicketTypeVO;

/**
 * 活動資訊數據訪問實現類 創建者: archchang 創建日期: 2025-05-07
 */
@Repository
public class EventInfoDAOImpl implements EventInfoDAO {
	@PersistenceContext
	private Session session;

	@Override
	public EventBuyVO getEventInfoById(Integer eventId) {
		return session.get(EventBuyVO.class, eventId);
	}

	@Override
	public List<EventBuyVO> getRecommendedEvents(int limit) {
		String hql = "FROM EventBuyVO e " + "WHERE e.posted = 1 AND e.eventFromDate > CURRENT_TIMESTAMP "
				+ "ORDER BY e.createTime DESC";
		return session.createQuery(hql, EventBuyVO.class).setMaxResults(limit).getResultList();
	}

	@Override
	public List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId) {
		String hql = "FROM TicketTypeVO WHERE eventId = :eventId ORDER BY price ASC";

		return session.createQuery(hql, TicketTypeVO.class).setParameter("eventId", eventId).getResultList();
	}

	@Override
	public Integer calculateRemainingTickets(Integer typeId) {
		String sql = "SELECT GREATEST(0, " +
                "ett.capacity - COALESCE(" +
                "(SELECT COUNT(*) FROM buyer_ticket bt " +
                "JOIN buyer_order bo ON bt.order_id = bo.order_id " +
                "WHERE bt.type_id = ett.type_id AND bo.is_paid = 1), 0)" +
                ") AS remaining_tickets " +
                "FROM event_ticket_type ett " +
                "WHERE ett.type_id = :typeId";

		Object result = session.createNativeQuery(sql).setParameter("typeId", typeId).uniqueResult();

		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public Integer checkFavoriteStatus(Integer memberId, Integer eventId) {
		String hql = "SELECT f.followed FROM FavoriteVO f " + "WHERE f.memberId = :memberId AND f.eventId = :eventId";

		return session.createQuery(hql, Integer.class).setParameter("memberId", memberId)
				.setParameter("eventId", eventId).uniqueResult();
	}

	@Override
	public boolean insertFavorite(FavoriteVO favorite) {
		
		session.persist(favorite);
		return true;

	}

	@Override
	public boolean updateFavorite(FavoriteVO favorite) {
		
		String hql = "UPDATE FavoriteVO SET followed = :followed "
				+ "WHERE memberId = :memberId AND eventId = :eventId";

		int rowsAffected = session.createQuery(hql).setParameter("followed", favorite.getFollowed())
				.setParameter("memberId", favorite.getMemberId()).setParameter("eventId", favorite.getEventId())
				.executeUpdate();

		return rowsAffected > 0;

	}

	@Override
	public Integer calculateTotalRemainingTickets(Integer eventId) {
		String sql = "SELECT COALESCE(SUM(GREATEST(0, " +
                "ett.capacity - COALESCE(" +
                "(SELECT COUNT(*) FROM buyer_ticket bt " +
                "JOIN buyer_order bo ON bt.order_id = bo.order_id " +
                "WHERE bt.type_id = ett.type_id AND bo.is_paid = 1), 0)" +
                ")), 0) AS total_remaining " +
                "FROM event_ticket_type ett " +
                "WHERE ett.event_id = :eventId";

		Object result = session.createNativeQuery(sql).setParameter("eventId", eventId).uniqueResult();

		return result != null ? ((Number) result).intValue() : 0;
	}

	@Override
	public byte[] getEventImage(Integer eventId) {
		String hql = "SELECT e.image FROM EventBuyVO e WHERE e.eventId = :eventId";

		return session.createQuery(hql, byte[].class)
                .setParameter("eventId", eventId)
                .uniqueResult();
	}

	// 新增：Stream 方式獲取圖片
	@Override
	public InputStream getEventImageStream(Integer eventId) {
		
		String hql = "SELECT e.image FROM EventBuyVO e WHERE e.eventId = :eventId";
		byte[] imageData = session.createQuery(hql, byte[].class).setParameter("eventId", eventId)
				.uniqueResult();

		if (imageData != null && imageData.length > 0) {
			return new ByteArrayInputStream(imageData);
		}
		return null;

	}
}