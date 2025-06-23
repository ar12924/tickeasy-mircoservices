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
 * 換票留言資料存取實作類
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */

@Repository("managerSwapCommentDAOImpl")
public class ManagerSwapCommentDAOImpl implements ManagerSwapCommentDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
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
        if (obj instanceof Boolean) {
            // TINYINT(1) 被 MySQL 轉為 Boolean，需要轉回數字
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
    
    @Override
    public List<ManagerSwapCommentVO> findSwapsWithPaging(Map<String, Object> params, Integer offset, Integer limit, String orderBy) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("sc.comment_id, sc.comment_member_id, sc.comment_ticket_id, ");
        sqlBuilder.append("sc.comment_description, sc.swapped_status, sc.swapped_time, ");
        sqlBuilder.append("sc.post_id, sc.create_time, sc.update_time, ");
        sqlBuilder.append("sp.post_member_id, sp.post_ticket_id, sp.post_description, sp.event_id ");
        sqlBuilder.append("FROM swap_comment sc ");
        sqlBuilder.append("JOIN swap_post sp ON sc.post_id = sp.post_id ");
        sqlBuilder.append("WHERE 1=1 ");
        
        // 活動ID條件
        if (params.get("eventId") != null) {
            sqlBuilder.append("AND sp.event_id = :eventId ");
        }
        
        // 關鍵字搜尋條件
        if (params.get("keyword") != null) {
            sqlBuilder.append("AND (sc.comment_description LIKE :keyword OR sp.post_description LIKE :keyword) ");
        }
        
        // 建立時間範圍條件
        if (params.get("createStartDate") != null) {
            sqlBuilder.append("AND sc.create_time >= :createStartDate ");
        }
        if (params.get("createEndDate") != null) {
            sqlBuilder.append("AND sc.create_time <= :createEndDate ");
        }
        
        // 換票時間範圍條件
        if (params.get("swappedStartDate") != null) {
            sqlBuilder.append("AND sc.swapped_time >= :swappedStartDate ");
        }
        if (params.get("swappedEndDate") != null) {
            sqlBuilder.append("AND sc.swapped_time <= :swappedEndDate ");
        }
        
        // 換票狀態條件
        if (params.get("swappedStatus") != null) {
            sqlBuilder.append("AND sc.swapped_status = :swappedStatus ");
        }
        
        // 排序
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            sqlBuilder.append("ORDER BY ").append(orderBy).append(" ");
        } else {
            sqlBuilder.append("ORDER BY sc.create_time DESC ");
        }
        
        NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(sqlBuilder.toString());
        
        // 設定參數
        setQueryParameters(query, params);
        
        // 設定分頁
        if (offset != null) {
            query.setFirstResult(offset);
        }
        if (limit != null) {
            query.setMaxResults(limit);
        }
        
        List<Object[]> results = query.getResultList();
        List<ManagerSwapCommentVO> swapComments = new ArrayList<>();
        
        // 使用安全的類型轉換
        for (Object[] result : results) {
            ManagerSwapCommentVO comment = new ManagerSwapCommentVO();
            
            // 使用安全轉換方法處理所有可能的類型問題
            comment.setCommentId(safeToInteger(result[0]));
            comment.setCommentMemberId(safeToInteger(result[1]));
            comment.setCommentTicketId(safeToInteger(result[2]));
            comment.setCommentDescription((String) result[3]);
            comment.setSwappedStatus(safeToInteger(result[4])); // 關鍵：安全處理 TINYINT(1)
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
    
    @Override
    public Long countSwaps(Map<String, Object> params) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) ");
        sqlBuilder.append("FROM swap_comment sc ");
        sqlBuilder.append("JOIN swap_post sp ON sc.post_id = sp.post_id ");
        sqlBuilder.append("WHERE 1=1 ");
        
        // 條件與上面相同...
        if (params.get("eventId") != null) {
            sqlBuilder.append("AND sp.event_id = :eventId ");
        }
        if (params.get("keyword") != null) {
            sqlBuilder.append("AND (sc.comment_description LIKE :keyword OR sp.post_description LIKE :keyword) ");
        }
        if (params.get("createStartDate") != null) {
            sqlBuilder.append("AND sc.create_time >= :createStartDate ");
        }
        if (params.get("createEndDate") != null) {
            sqlBuilder.append("AND sc.create_time <= :createEndDate ");
        }
        if (params.get("swappedStartDate") != null) {
            sqlBuilder.append("AND sc.swapped_time >= :swappedStartDate ");
        }
        if (params.get("swappedEndDate") != null) {
            sqlBuilder.append("AND sc.swapped_time <= :swappedEndDate ");
        }
        if (params.get("swappedStatus") != null) {
            sqlBuilder.append("AND sc.swapped_status = :swappedStatus ");
        }
        
        NativeQuery<BigInteger> query = getCurrentSession().createNativeQuery(sqlBuilder.toString());
        setQueryParameters(query, params);
        
        BigInteger result = query.getSingleResult();
        return result.longValue();
    }
    
    private void setQueryParameters(NativeQuery<?> query, Map<String, Object> params) {
        if (params.get("eventId") != null) {
            query.setParameter("eventId", params.get("eventId"));
        }
        if (params.get("keyword") != null) {
            String keyword = "%" + params.get("keyword").toString().trim() + "%";
            query.setParameter("keyword", keyword);
        }
        if (params.get("createStartDate") != null) {
            query.setParameter("createStartDate", params.get("createStartDate"));
        }
        if (params.get("createEndDate") != null) {
            query.setParameter("createEndDate", params.get("createEndDate"));
        }
        if (params.get("swappedStartDate") != null) {
            query.setParameter("swappedStartDate", params.get("swappedStartDate"));
        }
        if (params.get("swappedEndDate") != null) {
            query.setParameter("swappedEndDate", params.get("swappedEndDate"));
        }
        if (params.get("swappedStatus") != null) {
            query.setParameter("swappedStatus", params.get("swappedStatus"));
        }
    }
    
    @Override
    public List<Map<String, Object>> findEventList() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT sp.event_id, ei.event_name ");
        sqlBuilder.append("FROM swap_comment sc ");
        sqlBuilder.append("JOIN swap_post sp ON sc.post_id = sp.post_id ");
        sqlBuilder.append("JOIN event_info ei ON sp.event_id = ei.event_id ");
        sqlBuilder.append("ORDER BY ei.event_name");
        
        NativeQuery<Object[]> query = getCurrentSession().createNativeQuery(sqlBuilder.toString());
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
}