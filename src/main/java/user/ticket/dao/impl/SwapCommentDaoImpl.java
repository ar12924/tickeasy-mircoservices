package user.ticket.dao.impl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.ticket.dao.SwapCommentDao;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;
import user.ticket.vo.MemberVO;
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
    public List<Map<String, Object>> listSwapCommentsByPostId(Integer postId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.postId = :postId ORDER BY sc.createTime ASC";
        List<SwapCommentVO> swapComments = session
                .createQuery(hql, SwapCommentVO.class)
                .setParameter("postId", postId)
                .getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapCommentVO comment : swapComments) {
            result.add(convertSwapCommentToMap(comment));
        }
        return result;
    }

    @Override
    public Map<String, Object> getSwapCommentById(Integer commentId) {
        SwapCommentVO comment = session.get(SwapCommentVO.class, commentId);
        return comment != null ? convertSwapCommentToMap(comment) : null;
    }

    @Override
    public Map<String, Object> saveSwapComment(Integer postId, Integer memberId, Integer ticketId, String description) {
        SwapCommentVO swapComment = new SwapCommentVO();
        LocalDateTime now = LocalDateTime.now();
        swapComment.setPostId(postId);
        swapComment.setCommentMemberId(memberId);
        swapComment.setCommentTicketId(ticketId);
        swapComment.setCommentDescription(description);
        swapComment.setSwappedStatus(0); // 待換票
        swapComment.setCreateTime(now);
        swapComment.setUpdateTime(now);

        session.persist(swapComment);
        return convertSwapCommentToMap(swapComment);
    }

    @Override
    public boolean updateSwapCommentStatus(Integer commentId, Integer status) {
        SwapCommentVO comment = session.get(SwapCommentVO.class, commentId);
        if (comment != null) {
            comment.setSwappedStatus(status);
            if (status == 2) { // 已完成
                comment.setSwappedTime(LocalDateTime.now());
            }
            comment.setUpdateTime(LocalDateTime.now());
            session.merge(comment);
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
    public List<Map<String, Object>> listSwapCommentsByMemberId(Integer memberId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentMemberId = :memberId ORDER BY sc.createTime DESC";
        List<SwapCommentVO> swapComments = session
                .createQuery(hql, SwapCommentVO.class)
                .setParameter("memberId", memberId)
                .getResultList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapCommentVO comment : swapComments) {
            result.add(convertSwapCommentToMap(comment));
        }
        return result;
    }

    @Override
    public Map<String, Object> getSwapCommentByTicketId(Integer ticketId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId";
        List<SwapCommentVO> results = session
                .createQuery(hql, SwapCommentVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : convertSwapCommentToMap(results.get(0));
    }
    
    @Override
    public InputStream getMemberPhotoStream(Integer memberId) {
        try {
            MemberVO member = session.get(MemberVO.class, memberId);
            if (member != null && member.getPhoto() != null && member.getPhoto().length > 0) {
                return new ByteArrayInputStream(member.getPhoto());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getMemberPhoto(Integer memberId) {
        MemberVO member = session.get(MemberVO.class, memberId);
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

    /**
     * 將SwapCommentVO物件轉換為Map
     */
    private Map<String, Object> convertSwapCommentToMap(SwapCommentVO comment) {
        Map<String, Object> commentInfo = new HashMap<>();
        
        commentInfo.put("commentId", comment.getCommentId());
        commentInfo.put("commentDescription", comment.getCommentDescription());
        commentInfo.put("swappedStatus", comment.getSwappedStatus());
        commentInfo.put("createTime", comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        if (comment.getSwappedTime() != null) {
            commentInfo.put("swappedTime", comment.getSwappedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 查詢會員資訊
        MemberVO member = getMemberById(comment.getCommentMemberId());
        if (member != null) {
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickName", member.getNickName());
            commentInfo.put("member", memberInfo);
        }
        
        // 查詢票券資訊
        BuyerTicketVO ticket = getBuyerTicketById(comment.getCommentTicketId());
        if (ticket != null) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket.getTicketId());
            ticketInfo.put("participantName", ticket.getParticipantName());
            ticketInfo.put("eventName", ticket.getEventName());
            
            // 查詢票種資訊
            if (ticket.getTypeId() != null) {
                EventTicketTypeVO ticketType = getEventTicketTypeById(ticket.getTypeId());
                if (ticketType != null) {
                    ticketInfo.put("categoryName", ticketType.getCategoryName());
                    ticketInfo.put("price", ticketType.getPrice());
                }
            }
            
            commentInfo.put("ticket", ticketInfo);
        }
        
        return commentInfo;
    }

    private MemberVO getMemberById(Integer memberId) {
        String hql = "FROM MemberVO m WHERE m.memberId = :memberId";
        List<MemberVO> results = session
                .createQuery(hql, MemberVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private BuyerTicketVO getBuyerTicketById(Integer ticketId) {
        String hql = "FROM BuyerTicketVO bt WHERE bt.ticketId = :ticketId";
        List<BuyerTicketVO> results = session
                .createQuery(hql, BuyerTicketVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private EventTicketTypeVO getEventTicketTypeById(Integer typeId) {
        String hql = "FROM EventTicketTypeVO ett WHERE ett.typeId = :typeId";
        List<EventTicketTypeVO> results = session
                .createQuery(hql, EventTicketTypeVO.class)
                .setParameter("typeId", typeId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}