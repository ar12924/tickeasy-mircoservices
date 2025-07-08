package user.ticket.dao.impl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.member.vo.Member;
import user.ticket.dao.SwapCommentDao;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;
import user.ticket.vo.SwapCommentVO;
/**
 * 換票留言資料存取實作類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
@Repository
public class SwapCommentDaoImpl implements SwapCommentDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<SwapCommentVO> listSwapCommentsByPostId(Integer postId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.postId = :postId ORDER BY sc.createTime ASC";
        return session.createQuery(hql, SwapCommentVO.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public SwapCommentVO getSwapCommentById(Integer commentId) {
    	return session.get(SwapCommentVO.class, commentId);
    }

    @Override
    public SwapCommentVO saveSwapComment(Integer postId, Integer memberId, Integer ticketId, String description) {
        SwapCommentVO swapComment = new SwapCommentVO();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        swapComment.setPostId(postId);
        swapComment.setCommentMemberId(memberId);
        swapComment.setCommentTicketId(ticketId);
        swapComment.setCommentDescription(description);
        swapComment.setSwappedStatus(0); // 待換票
        swapComment.setCreateTime(now);
        swapComment.setUpdateTime(now);

        session.persist(swapComment);
        session.flush();
        session.refresh(swapComment);
        return swapComment;
    }

    @Override
    public boolean updateSwapCommentStatus(Integer commentId, Integer status) {
    	SwapCommentVO comment = session.get(SwapCommentVO.class, commentId);
        if (comment != null) {
            comment.setSwappedStatus(status);
            if (status == 2) { // 已完成
                comment.setSwappedTime(new Timestamp(System.currentTimeMillis()));
            }
            comment.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            
            // 使用merge確保更新
            session.merge(comment);
            session.flush(); // 強制寫入
            session.evict(comment); //清除快取
            return true;
        }
        return false;
    }

    @Override
    public void removeSwapComment(Integer commentId) {
        SwapCommentVO comment = session.get(SwapCommentVO.class, commentId);
        if (comment != null) {
            session.remove(comment);
        }
    }

    @Override
    public List<SwapCommentVO> listSwapCommentsByMemberId(Integer memberId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentMemberId = :memberId ORDER BY sc.createTime DESC";
        return session.createQuery(hql, SwapCommentVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public SwapCommentVO getSwapCommentByTicketId(Integer ticketId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId";
        List<SwapCommentVO> results = session.createQuery(hql, SwapCommentVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public InputStream getMemberPhotoStream(Integer memberId) {
     
    	Member member = session.get(Member.class, memberId);
       	if (member != null && member.getPhoto() != null && member.getPhoto().length > 0) {
       		return new ByteArrayInputStream(member.getPhoto());
        }
        return null;
    }

    @Override
    public byte[] getMemberPhoto(Integer memberId) {
    	Member member = session.get(Member.class, memberId);
        return (member != null) ? member.getPhoto() : null;
    }

    @Override
    public boolean existsSwapCommentByTicketId(Integer ticketId) {
        String hql = "SELECT COUNT(sc) FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId";
        Long count = session
                .createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean hasCommentPermission(Integer commentId, Integer memberId) {
        String hql = "SELECT COUNT(sc) FROM SwapCommentVO sc WHERE sc.commentId = :commentId AND sc.commentMemberId = :memberId";
        Long count = session
                .createQuery(hql, Long.class)
                .setParameter("commentId", commentId)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean isPostOwnerByCommentId(Integer commentId, Integer memberId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp, SwapCommentVO sc WHERE sc.commentId = :commentId AND sc.postId = sp.postId AND sp.postMemberId = :memberId";
        Long count = session
                .createQuery(hql, Long.class)
                .setParameter("commentId", commentId)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Member getMemberById(Integer memberId) {
        String hql = "FROM Member m WHERE m.memberId = :memberId";
        List<Member> results = session
                .createQuery(hql, Member.class)
                .setParameter("memberId", memberId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public BuyerTicketVO getBuyerTicketById(Integer ticketId) {
        String hql = "FROM BuyerTicketVO bt WHERE bt.ticketId = :ticketId";
        List<BuyerTicketVO> results = session
                .createQuery(hql, BuyerTicketVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public EventTicketTypeVO getEventTicketTypeById(Integer typeId) {
        String hql = "FROM EventTicketTypeVO ett WHERE ett.typeId = :typeId";
        List<EventTicketTypeVO> results = session
                .createQuery(hql, EventTicketTypeVO.class)
                .setParameter("typeId", typeId)
                .getResultList();
        
        if (!results.isEmpty()) {
            EventTicketTypeVO result = results.get(0);
            return result;
        } else {
            return null;
        }
    }
    
    @Override
    public List<SwapCommentVO> findCommentsByTicketId(Integer ticketId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId";
        return session.createQuery(hql, SwapCommentVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
    }
    
    @Override
    public List<Map<String, Object>> listSwapCommentsWithDetailsByPostId(Integer postId) {
    	String sql = "SELECT " +
                "sc.comment_id, " +
                "sc.comment_description, " +
                "sc.swapped_status, " +
                "sc.swapped_time, " +
                "sc.create_time, " +
                "sc.update_time, " +
                "sc.comment_member_id, " +
                "sc.comment_ticket_id, " +
                "m.nick_name as member_nick_name, " +
                "bt.participant_name, " +
                "bt.price as ticket_price, " +
                "ett.category_name " +
                "FROM swap_comment sc " +
                "LEFT JOIN member m ON sc.comment_member_id = m.member_id " +
                "LEFT JOIN buyer_ticket bt ON sc.comment_ticket_id = bt.ticket_id " +
                "LEFT JOIN event_ticket_type ett ON bt.type_id = ett.type_id " +
                "WHERE sc.post_id = ? " +
                "ORDER BY sc.create_time ASC";
        
        // 強制清除快取確保資料最新
        session.flush();
        session.clear();
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = session.createNativeQuery(sql)
            .setParameter(1, postId)
            .getResultList();
            
        List<Map<String, Object>> comments = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> comment = new HashMap<>();
            comment.put("commentId", convertToInteger(row[0]));
            comment.put("commentDescription", row[1]);
            Integer swappedStatus = convertToInteger(row[2]);
            comment.put("swappedStatus", swappedStatus != null ? swappedStatus : 0);  // 確保是整數
            comment.put("swappedTime", row[3]);
            comment.put("createTime", row[4]);
            comment.put("updateTime", row[5]);
            comment.put("commentMemberId", convertToInteger(row[6]));
            comment.put("commentTicketId", convertToInteger(row[7]));
            
            // 統一票券資訊結構
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("participantName", row[9]);
            ticketInfo.put("price", row[10]);
            ticketInfo.put("categoryName", row[11]);
            comment.put("ticket", ticketInfo);
            
            // 統一會員資訊結構 - 修正：按照前端期望的結構
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", convertToInteger(row[6]));
            memberInfo.put("nickName", row[8]); // 注意：nickName不是memberNickName
            comment.put("member", memberInfo);
            
            comments.add(comment);
        }
        
        return comments;
    }

    @Override
    public List<Map<String, Object>> listSwapCommentsWithDetailsByMemberId(Integer memberId) {
    	session.flush();
        session.clear();
    	// 第一階段：查詢留言基本資訊 + 會員資訊 (2個表)
        String sql1 = "SELECT " +
                "sc.comment_id, " +
                "sc.comment_description, " +
                "sc.swapped_status, " +
                "sc.swapped_time, " +
                "sc.create_time, " +
                "sc.update_time, " +
                "sc.comment_member_id, " +
                "sc.comment_ticket_id, " +
                "m.nick_name as member_nick_name " +
                "FROM swap_comment sc " +
                "LEFT JOIN member m ON sc.comment_member_id = m.member_id " +
                "WHERE sc.comment_member_id = :memberId " +
                "ORDER BY sc.create_time DESC";

        @SuppressWarnings("unchecked")
        List<Object[]> basicResults = session.createNativeQuery(sql1)
                .setParameter("memberId", memberId)
                .getResultList();

        if (basicResults.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有票券ID
        List<Integer> ticketIds = new ArrayList<>();
        for (Object[] row : basicResults) {
            Integer ticketId = convertToInteger(row[7]);
            if (ticketId != null && !ticketIds.contains(ticketId)) {
                ticketIds.add(ticketId);
            }
        }

        // 第二階段：批量查詢票券資訊 + 票種資訊 (2個表)
        Map<Integer, Map<String, Object>> ticketInfoMap = new HashMap<>();
        if (!ticketIds.isEmpty()) {
            String sql2 = "SELECT " +
                    "bt.ticket_id, " +
                    "bt.participant_name, " +
                    "bt.price as ticket_price, " +
                    "bt.type_id, " +
                    "ett.category_name, " +
                    "ett.price as type_price " +
                    "FROM buyer_ticket bt " +
                    "LEFT JOIN event_ticket_type ett ON bt.type_id = ett.type_id " +
                    "WHERE bt.ticket_id IN (:ticketIds)";

            @SuppressWarnings("unchecked")
            List<Object[]> ticketResults = session.createNativeQuery(sql2)
                    .setParameter("ticketIds", ticketIds)
                    .getResultList();

            for (Object[] row : ticketResults) {
                Integer ticketId = (Integer) row[0];
                Map<String, Object> ticketInfo = new HashMap<>();
                ticketInfo.put("ticketId", ticketId);
                ticketInfo.put("participantName", row[1]);
                ticketInfo.put("price", row[2]);
                ticketInfo.put("typeId", row[3]);
                ticketInfo.put("categoryName", row[4]);
                ticketInfoMap.put(ticketId, ticketInfo);
            }
        }

        // 組合最終結果
        List<Map<String, Object>> comments = new ArrayList<>();
        for (Object[] row : basicResults) {
            Map<String, Object> commentInfo = new HashMap<>();
            
            // 留言基本資訊
            commentInfo.put("commentId", convertToInteger(row[0]));
            commentInfo.put("commentDescription", row[1]);
            Integer swappedStatus = convertToInteger(row[2]);
            commentInfo.put("swappedStatus", swappedStatus != null ? swappedStatus : 0);
            commentInfo.put("swappedTime", row[3]);
            commentInfo.put("createTime", row[4]);
            commentInfo.put("updateTime", row[5]);
            
            // 會員資訊
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", convertToInteger(row[6]));
            memberInfo.put("nickName", row[8]);
            memberInfo.put("photoUrl", "/api/member-photos/" + convertToInteger(row[6]));
            commentInfo.put("member", memberInfo);
            
            // 票券資訊
            Integer ticketId = convertToInteger(row[7]);
            Map<String, Object> ticketInfo = ticketInfoMap.getOrDefault(ticketId, new HashMap<>());
            if (ticketInfo.isEmpty() && ticketId != null) {
                // 如果沒找到票券資訊，建立預設值
                ticketInfo.put("ticketId", ticketId);
                ticketInfo.put("participantName", "未知");
                ticketInfo.put("price", 0);
                ticketInfo.put("categoryName", "未知票種");
            }
            commentInfo.put("ticket", ticketInfo);
            
            comments.add(commentInfo);
        }

        return comments;
    }
    
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return 0;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof BigInteger) {
            return ((BigInteger) value).intValue();
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        
        return 0;
    }
}