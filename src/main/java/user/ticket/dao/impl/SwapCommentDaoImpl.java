package user.ticket.dao.impl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
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
import user.member.vo.Member;
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
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public List<SwapCommentVO> findCommentsByTicketId(Integer ticketId) {
        String hql = "FROM SwapCommentVO sc WHERE sc.commentTicketId = :ticketId";
        return session.createQuery(hql, SwapCommentVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
    }
}