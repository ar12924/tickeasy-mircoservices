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

import user.ticket.dao.SwapPostDao;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.EventTicketTypeVO;
import user.member.vo.Member;
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
        SwapPostVO post = session.get(SwapPostVO.class, postId);
        return post;
    }

    @Override
    public SwapPostVO saveSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId) {
        SwapPostVO swapPost = new SwapPostVO();
        LocalDateTime now = LocalDateTime.now();
        swapPost.setPostMemberId(memberId);
        swapPost.setPostTicketId(ticketId);
        swapPost.setPostDescription(description);
        swapPost.setEventId(eventId);
        swapPost.setCreateTime(now);
        swapPost.setUpdateTime(now);
        
        session.persist(swapPost);
        return swapPost;
    }

    @Override
    public SwapPostVO updateSwapPost(Integer postId, String description) {
        SwapPostVO swapPost = session.get(SwapPostVO.class, postId);
        if (swapPost != null) {
            swapPost.setPostDescription(description);
            swapPost.setUpdateTime(LocalDateTime.now());
            session.merge(swapPost);
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
        String hql = "FROM EventInfoVO e WHERE e.eventId = :eventId";
        List<EventInfoVO> results = session
                .createQuery(hql, EventInfoVO.class)
                .setParameter("eventId", eventId)
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
}
