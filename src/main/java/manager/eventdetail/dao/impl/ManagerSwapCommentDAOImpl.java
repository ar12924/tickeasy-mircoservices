package manager.eventdetail.dao.impl;

import manager.eventdetail.dao.ManagerSwapCommentDAO;
import manager.eventdetail.vo.ManagerSwapCommentVO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 換票留言資料存取實作類 創建者: archchang 創建日期: 2025-06-23
 */

@Repository("managerSwapCommentDAOImpl")
public class ManagerSwapCommentDAOImpl implements ManagerSwapCommentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final String BASE_SELECT = 
		    "SELECT sc.comment_id, sc.comment_member_id, sc.comment_ticket_id, " +
		    "sc.comment_description, " +
		    "CAST(sc.swapped_status AS SIGNED) as swapped_status, " +
		    "sc.swapped_time, sc.post_id, sc.create_time, sc.update_time, " +
		    "sp.post_member_id, sp.post_ticket_id, sp.post_description, sp.event_id " +
		    "FROM swap_comment sc " +
		    "JOIN swap_post sp ON sc.post_id = sp.post_id " +
		    "WHERE 1=1 ";

	@Override
	public List<ManagerSwapCommentVO> findByCreateTime(Integer eventId, String keyword, Date startDate, Date endDate,
			Integer swappedStatus, Integer offset, Integer limit) {
		StringBuilder sql = new StringBuilder(BASE_SELECT);
		Map<String, Object> params = new HashMap<>();

		addCommonWhereConditions(sql, params, eventId, keyword, swappedStatus);
		addTimeRangeConditions(sql, params, "sc.create_time", startDate, endDate);
		sql.append("ORDER BY sc.create_time DESC ");

		return executeQuery(sql.toString(), params, offset, limit);
	}

	@Override
	public List<ManagerSwapCommentVO> findBySwappedTime(Integer eventId, String keyword, Date startDate, Date endDate,
			Integer swappedStatus, Integer offset, Integer limit) {
		StringBuilder sql = new StringBuilder(BASE_SELECT);
		Map<String, Object> params = new HashMap<>();

		addCommonWhereConditions(sql, params, eventId, keyword, swappedStatus);
		addTimeRangeConditions(sql, params, "sc.swapped_time", startDate, endDate);
		sql.append("ORDER BY sc.swapped_time DESC ");

		return executeQuery(sql.toString(), params, offset, limit);
	}

	@Override
	public Long countByCreateTime(Integer eventId, String keyword, Date startDate, Date endDate,
			Integer swappedStatus) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM swap_comment sc JOIN swap_post sp ON sc.post_id = sp.post_id WHERE 1=1 ");
		Map<String, Object> params = new HashMap<>();

		addCommonWhereConditions(sql, params, eventId, keyword, swappedStatus);
		addTimeRangeConditions(sql, params, "sc.create_time", startDate, endDate);

		return executeCountQuery(sql.toString(), params);
	}

	@Override
	public Long countBySwappedTime(Integer eventId, String keyword, Date startDate, Date endDate,
			Integer swappedStatus) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM swap_comment sc JOIN swap_post sp ON sc.post_id = sp.post_id WHERE 1=1 ");
		Map<String, Object> params = new HashMap<>();

		addCommonWhereConditions(sql, params, eventId, keyword, swappedStatus);
		addTimeRangeConditions(sql, params, "sc.swapped_time", startDate, endDate);

		return executeCountQuery(sql.toString(), params);
	}

	@Override
	public List<Map<String, Object>> findEventList() {
		String sql = "SELECT DISTINCT sp.event_id, ei.event_name " +
                "FROM swap_comment sc " +
                "JOIN swap_post sp ON sc.post_id = sp.post_id " +
                "JOIN event_info ei ON sp.event_id = ei.event_id " +
                "ORDER BY ei.event_name";

		NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(sql);
		List<Object[]> results = query.getResultList();

		List<Map<String, Object>> eventList = new ArrayList<>();
		for (Object[] result : results) {
			Map<String, Object> event = new HashMap<>();
			event.put("eventId", safeToInteger(result[0]));
			event.put("eventName", result[1]);
			eventList.add(event);
		}

		return eventList;
	}

	// === 私有方法 ===

	private void addCommonWhereConditions(StringBuilder sql, Map<String, Object> params, Integer eventId,
			String keyword, Integer swappedStatus) {
		if (eventId != null) {
			sql.append("AND sp.event_id = :eventId ");
			params.put("eventId", eventId);
		}

		if (keyword != null && !keyword.trim().isEmpty()) {
			sql.append("AND (sc.comment_description LIKE :keyword OR sp.post_description LIKE :keyword) ");
			params.put("keyword", "%" + keyword.trim() + "%");
		}

		if (swappedStatus != null) {
			sql.append("AND sc.swapped_status = :swappedStatus ");
			params.put("swappedStatus", swappedStatus);
		}
	}

	private void addTimeRangeConditions(StringBuilder sql, Map<String, Object> params, String timeColumn,
			Date startDate, Date endDate) {
		if (startDate != null) {
			sql.append("AND ").append(timeColumn).append(" >= :startDate ");
			params.put("startDate", startDate);
		}

		if (endDate != null) {
			sql.append("AND ").append(timeColumn).append(" <= :endDate ");
			params.put("endDate", endDate);
		}
	}

	private List<ManagerSwapCommentVO> executeQuery(String sql, Map<String, Object> params, Integer offset,
			Integer limit) {
		NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(sql);
		setQueryParameters(query, params);

		if (offset != null && offset >= 0) {
			query.setFirstResult(offset);
		}
		if (limit != null && limit > 0) {
			query.setMaxResults(limit);
		}

		List<Object[]> results = query.getResultList();
		return convertToManagerSwapCommentVO(results);
	}

	private Long executeCountQuery(String sql, Map<String, Object> params) {
		NativeQuery<Number> query = getCurrentSession().createNativeQuery(sql);
		setQueryParameters(query, params);
		return query.getSingleResult().longValue();
	}

	private void setQueryParameters(NativeQuery<?> query, Map<String, Object> params) {
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
	}

	private List<ManagerSwapCommentVO> convertToManagerSwapCommentVO(List<Object[]> results) {
		List<ManagerSwapCommentVO> swapComments = new ArrayList<>();

		for (Object[] result : results) {
			ManagerSwapCommentVO comment = new ManagerSwapCommentVO();

			comment.setCommentId(safeToInteger(result[0]));
			comment.setCommentMemberId(safeToInteger(result[1]));
			comment.setCommentTicketId(safeToInteger(result[2]));
			comment.setCommentDescription((String) result[3]);
			comment.setSwappedStatus(safeToInteger(result[4]));
			comment.setSwappedTime(safeToDate(result[5]));
			comment.setPostId(safeToInteger(result[6]));
			comment.setCreateTime(safeToDate(result[7]));
			comment.setUpdateTime(safeToDate(result[8]));

			// 設定關聯的貼文資訊
			comment.setPostMemberId(safeToInteger(result[9]));
			comment.setPostTicketId(safeToInteger(result[10]));
			comment.setPostDescription((String) result[11]);
			comment.setEventId(safeToInteger(result[12]));

			swapComments.add(comment);
		}

		return swapComments;
	}

	/**
	 * 安全地將 Object 轉換為 Integer - 特別處理 TINYINT(1) 轉 Boolean 的問題
	 */
	private Integer safeToInteger(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Byte) {
			return ((Byte) obj).intValue();
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj) ? 1 : 0;
		}
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		try {
			return Integer.valueOf(obj.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 安全地將 Object 轉換為 Date
	 */
	private Date safeToDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Date) {
			return (Date) obj;
		}
		if (obj instanceof Timestamp) {
			return new Date(((Timestamp) obj).getTime());
		}
		return null;
	}
}