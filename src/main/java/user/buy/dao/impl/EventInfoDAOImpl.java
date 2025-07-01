package user.buy.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public EventBuyVO getEventInfoById(Integer eventId) {
		return getCurrentSession().get(EventBuyVO.class, eventId);
	}

	@Override
	public List<EventBuyVO> getRecommendedEvents(int limit) {
		String hql = "FROM EventBuyVO e " + "WHERE e.posted = 1 AND e.eventFromDate > CURRENT_TIMESTAMP "
				+ "ORDER BY e.createTime DESC";
		return getCurrentSession().createQuery(hql, EventBuyVO.class).setMaxResults(limit).getResultList();
	}

	@Override
	public List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId) {
		String hql = "FROM TicketTypeVO WHERE eventId = :eventId ORDER BY price ASC";

		List<TicketTypeVO> ticketTypes = getCurrentSession().createQuery(hql, TicketTypeVO.class)
				.setParameter("eventId", eventId).getResultList();

		// 計算每個票券類型的剩餘票數
		for (TicketTypeVO ticketType : ticketTypes) {
			Integer remainingTickets = calculateRemainingTickets(ticketType.getTypeId());
			ticketType.setRemainingTickets(remainingTickets);
		}

		return ticketTypes;
	}

	@Override
	public Integer calculateRemainingTickets(Integer typeId) {
		String sql = "SELECT ett.capacity - COALESCE(" + "  (SELECT COUNT(*) FROM buyer_ticket bt "
				+ "   JOIN buyer_order bo ON bt.order_id = bo.order_id "
				+ "   WHERE bt.type_id = ett.type_id AND bo.is_paid = 1), 0" + ") AS remaining_tickets "
				+ "FROM event_ticket_type ett " + "WHERE ett.type_id = :typeId";

		Object result = getCurrentSession().createNativeQuery(sql).setParameter("typeId", typeId).uniqueResult();

		Integer remainingTickets = null;
		if (result != null) {
			remainingTickets = ((Number) result).intValue();
		}

		return remainingTickets != null ? remainingTickets : 0;
	}

	@Override
	public Integer checkFavoriteStatus(Integer memberId, Integer eventId) {
		String hql = "SELECT f.followed FROM FavoriteVO f " + "WHERE f.memberId = :memberId AND f.eventId = :eventId";

		return getCurrentSession().createQuery(hql, Integer.class).setParameter("memberId", memberId)
				.setParameter("eventId", eventId).uniqueResult();
	}

	@Override
	public boolean insertFavorite(FavoriteVO favorite) {
		try {
			getCurrentSession().persist(favorite);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateFavorite(FavoriteVO favorite) {
		try {
			String hql = "UPDATE FavoriteVO SET followed = :followed "
					+ "WHERE memberId = :memberId AND eventId = :eventId";

			int rowsAffected = getCurrentSession().createQuery(hql).setParameter("followed", favorite.getFollowed())
					.setParameter("memberId", favorite.getMemberId()).setParameter("eventId", favorite.getEventId())
					.executeUpdate();

			return rowsAffected > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Integer calculateTotalRemainingTickets(Integer eventId) {
		List<TicketTypeVO> ticketTypes = getEventTicketTypesByEventId(eventId);

		int totalRemainingTickets = 0;
		for (TicketTypeVO ticketType : ticketTypes) {
			totalRemainingTickets += ticketType.getRemainingTickets();
		}

		return totalRemainingTickets;
	}

	@Override
	public byte[] getEventImage(Integer eventId) {
		String hql = "SELECT e.image FROM EventBuyVO e WHERE e.eventId = :eventId";

		byte[] image = getCurrentSession().createQuery(hql, byte[].class).setParameter("eventId", eventId)
				.uniqueResult();

		return image;
	}

	// 新增：Stream 方式獲取圖片
	@Override
	public InputStream getEventImageStream(Integer eventId) {
		try {
            String hql = "SELECT e.image FROM EventBuyVO e WHERE e.eventId = :eventId";
            byte[] imageData = getCurrentSession().createQuery(hql, byte[].class)
                    .setParameter("eventId", eventId)
                    .uniqueResult();
            
            if (imageData != null && imageData.length > 0) {
                return new ByteArrayInputStream(imageData);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
}