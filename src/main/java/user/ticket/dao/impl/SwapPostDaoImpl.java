package user.ticket.dao.impl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.member.vo.Member;
import user.ticket.dao.SwapPostDao;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.EventTicketTypeVO;
import user.ticket.vo.SwapPostVO;
/**
 * 換票貼文資料存取實作類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
@Repository
public class SwapPostDaoImpl implements SwapPostDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<SwapPostVO> listSwapPostsByEventId(Integer eventId) {
        String hql = "FROM SwapPostVO sp WHERE sp.eventId = :eventId ORDER BY sp.createTime DESC";
        return session.createQuery(hql, SwapPostVO.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    @Override
    public SwapPostVO getSwapPostById(Integer postId) {
    	return session.get(SwapPostVO.class, postId);
    }

    @Override
    public SwapPostVO saveSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId) {
        SwapPostVO swapPost = new SwapPostVO();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        swapPost.setPostMemberId(memberId);
        swapPost.setPostTicketId(ticketId);
        swapPost.setPostDescription(description);
        swapPost.setEventId(eventId);
        swapPost.setCreateTime(now);
        swapPost.setUpdateTime(now);
        
        session.persist(swapPost);
        session.flush();
        return swapPost;
    }

    @Override
    public SwapPostVO updateSwapPost(Integer postId, String description) {
        SwapPostVO swapPost = session.get(SwapPostVO.class, postId);
        if (swapPost != null) {
            swapPost.setPostDescription(description);
            swapPost.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            session.merge(swapPost);
            return swapPost;
        }
        return null;
    }

    @Override
    public void removeSwapPost(Integer postId) {
        SwapPostVO swapPost = session.get(SwapPostVO.class, postId);
        if (swapPost != null) {
            session.remove(swapPost);
        }
    }

    @Override
    public List<SwapPostVO> listSwapPostsByMemberId(Integer memberId) {
        String hql = "FROM SwapPostVO sp WHERE sp.postMemberId = :memberId ORDER BY sp.createTime DESC";
        return session.createQuery(hql, SwapPostVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public SwapPostVO getSwapPostByTicketId(Integer ticketId) {
        String hql = "FROM SwapPostVO sp WHERE sp.postTicketId = :ticketId";
        List<SwapPostVO> results = session
                .createQuery(hql, SwapPostVO.class)
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
    public boolean existsSwapPostByTicketId(Integer ticketId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp WHERE sp.postTicketId = :ticketId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean isPostOwner(Integer postId, Integer memberId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp WHERE sp.postId = :postId AND sp.postMemberId = :memberId";
        Long count = session
                .createQuery(hql, Long.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Member getMemberById(Integer memberId) {
    	return session.get(Member.class, memberId);
    }
    
    @Override
    public EventInfoVO getEventInfoById(Integer eventId) {
    	return session.get(EventInfoVO.class, eventId);
    }

    @Override
    public BuyerTicketVO getBuyerTicketById(Integer ticketId) {
    	return session.get(BuyerTicketVO.class, ticketId);
    }

    @Override
    public EventTicketTypeVO getEventTicketTypeById(Integer typeId) {
    	return session.get(EventTicketTypeVO.class, typeId);
    }
    
    @Override
    public Member getMemberByNickname(String nickname) {
        String hql = "FROM Member m WHERE m.nickName = :nickname AND m.isActive = 1";
        List<Member> results = session
        		.createQuery(hql, Member.class)
                .setParameter("nickname", nickname)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    // 查會員票券
    @Override
    public List<BuyerTicketVO> getUserTickets(Integer memberId) {
        String hql = "FROM BuyerTicketVO bt WHERE bt.currentHolderMemberId = :memberId " +
                     "AND bt.used = 0 ORDER BY bt.ticketId DESC";
        return session.createQuery(hql, BuyerTicketVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
    
    @Override
    public Long countPostsByTicketId(Integer ticketId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp WHERE sp.postTicketId = :ticketId";
        return session.createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
    }

    @Override
    public SwapPostVO getPostByCommentId(Integer commentId) {
        String hql = "SELECT sp FROM SwapPostVO sp, SwapCommentVO sc WHERE sc.commentId = :commentId AND sc.postId = sp.postId";
        List<SwapPostVO> results = session.createQuery(hql, SwapPostVO.class)
                .setParameter("commentId", commentId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    // 新增檢查會員是否已對特定活動發文的方法
    @Override
    public boolean hasEventPostByMember(Integer memberId, Integer eventId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp WHERE sp.postMemberId = :memberId AND sp.eventId = :eventId";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("memberId", memberId)
                .setParameter("eventId", eventId)
                .getSingleResult();
        return count > 0;
    }

    // 檢查票券是否已在留言中使用
    @Override
    public boolean isTicketUsedInComment(Integer ticketId) {
        String hql = "SELECT COUNT(sc) FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId AND sc.swappedStatus != 3";
        Long count = session.createQuery(hql, Long.class)
                .setParameter("ticketId", ticketId)
                .getSingleResult();
        return count > 0;
    }

    // 獲取會員在特定活動的所有貼文
    @Override
    public List<SwapPostVO> getMemberPostsByEvent(Integer memberId, Integer eventId) {
        String hql = "FROM SwapPostVO sp WHERE sp.postMemberId = :memberId AND sp.eventId = :eventId ORDER BY sp.createTime DESC";
        return session.createQuery(hql, SwapPostVO.class)
                .setParameter("memberId", memberId)
                .setParameter("eventId", eventId)
                .getResultList();
    }
    
    @Override
    public List<Map<String, Object>> listSwapPostsWithDetailsByEventId(Integer eventId) {
        // 第一階段：查詢貼文基本資訊 + 會員資訊 + 活動資訊 (3個表)
        String sql1 = "SELECT " +
                "sp.post_id, " +
                "sp.post_description, " +
                "sp.create_time, " +
                "sp.update_time, " +
                "sp.event_id, " +
                "sp.post_ticket_id, " +
                "sp.post_member_id, " +
                "m.nick_name as member_nick_name, " +
                "ei.event_name " +
                "FROM swap_post sp " +
                "LEFT JOIN member m ON sp.post_member_id = m.member_id " +
                "LEFT JOIN event_info ei ON sp.event_id = ei.event_id " +
                "WHERE sp.event_id = :eventId " +
                "ORDER BY sp.create_time DESC";

        @SuppressWarnings("unchecked")
        List<Object[]> basicResults = session.createNativeQuery(sql1)
                .setParameter("eventId", eventId)
                .getResultList();

        if (basicResults.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有票券ID
        List<Integer> ticketIds = new ArrayList<>();
        for (Object[] row : basicResults) {
            Integer ticketId = (Integer) row[5];
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
        List<Map<String, Object>> swapPosts = new ArrayList<>();
        for (Object[] row : basicResults) {
            Map<String, Object> postInfo = new HashMap<>();
            
            // 貼文基本資訊
            postInfo.put("postId", row[0]);
            postInfo.put("postDescription", row[1]);
            postInfo.put("createTime", row[2]);
            postInfo.put("updateTime", row[3]);
            
            // 會員資訊
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", row[6]);
            memberInfo.put("nickName", row[7]);
            memberInfo.put("photoUrl", "/api/member-photos/" + row[6]);
            postInfo.put("member", memberInfo);
            
            // 活動資訊
            Map<String, Object> eventInfo = new HashMap<>();
            eventInfo.put("eventId", row[4]);
            eventInfo.put("eventName", row[8]);
            postInfo.put("event", eventInfo);
            
            // 票券資訊
            Integer ticketId = (Integer) row[5];
            Map<String, Object> ticketInfo = ticketInfoMap.getOrDefault(ticketId, new HashMap<>());
            if (ticketInfo.isEmpty() && ticketId != null) {
                // 如果沒找到票券資訊，建立預設值
                ticketInfo.put("ticketId", ticketId);
                ticketInfo.put("participantName", "未知");
                ticketInfo.put("price", 0);
                ticketInfo.put("categoryName", "未知票種");
            }
            // 確保 eventName 存在於票券資訊中
            if (!ticketInfo.isEmpty()) {
                ticketInfo.put("eventName", row[8]);
            }
            postInfo.put("ticket", ticketInfo);
            
            swapPosts.add(postInfo);
        }

        return swapPosts;
    }

    @Override
    public List<Map<String, Object>> listSwapPostsWithDetailsByMemberId(Integer memberId) {
        // 第一階段：查詢貼文基本資訊 + 會員資訊 + 活動資訊 (3個表)
        String sql1 = "SELECT " +
                "sp.post_id, " +
                "sp.post_description, " +
                "sp.create_time, " +
                "sp.update_time, " +
                "sp.event_id, " +
                "sp.post_ticket_id, " +
                "sp.post_member_id, " +
                "m.nick_name as member_nick_name, " +
                "ei.event_name " +
                "FROM swap_post sp " +
                "LEFT JOIN member m ON sp.post_member_id = m.member_id " +
                "LEFT JOIN event_info ei ON sp.event_id = ei.event_id " +
                "WHERE sp.post_member_id = :memberId " +
                "ORDER BY sp.create_time DESC";

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
            Integer ticketId = (Integer) row[5];
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
        List<Map<String, Object>> swapPosts = new ArrayList<>();
        for (Object[] row : basicResults) {
            Map<String, Object> postInfo = new HashMap<>();
            
            // 貼文基本資訊
            postInfo.put("postId", row[0]);
            postInfo.put("postDescription", row[1]);
            postInfo.put("createTime", row[2]);
            postInfo.put("updateTime", row[3]);
            
            // 會員資訊
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", row[6]);
            memberInfo.put("nickName", row[7]);
            memberInfo.put("photoUrl", "/api/member-photos/" + row[6]);
            postInfo.put("member", memberInfo);
            
            // 活動資訊
            Map<String, Object> eventInfo = new HashMap<>();
            eventInfo.put("eventId", row[4]);
            eventInfo.put("eventName", row[8]);
            postInfo.put("event", eventInfo);
            
            // 票券資訊
            Integer ticketId = (Integer) row[5];
            Map<String, Object> ticketInfo = ticketInfoMap.getOrDefault(ticketId, new HashMap<>());
            if (ticketInfo.isEmpty() && ticketId != null) {
                // 如果沒找到票券資訊，建立預設值
                ticketInfo.put("ticketId", ticketId);
                ticketInfo.put("participantName", "未知");
                ticketInfo.put("price", 0);
                ticketInfo.put("categoryName", "未知票種");
            }
            // 確保 eventName 存在於票券資訊中
            if (!ticketInfo.isEmpty()) {
                ticketInfo.put("eventName", row[8]);
            }
            postInfo.put("ticket", ticketInfo);
            
            swapPosts.add(postInfo);
        }

        return swapPosts;
    }
}
