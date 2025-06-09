package user.ticket.dao.impl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import user.ticket.dao.SwapPostDao;
import user.ticket.vo.SwapPostVO;
import user.ticket.vo.MemberVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.persistence.PersistenceContext;
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
    public List<Map<String, Object>> listSwapPostsByEventId(Integer eventId) {
        String hql = "FROM SwapPostVO sp WHERE sp.eventId = :eventId ORDER BY sp.createTime DESC";
        List<SwapPostVO> swapPosts = session
                .createQuery(hql, SwapPostVO.class)
                .setParameter("eventId", eventId)
                .getResultList();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapPostVO post : swapPosts) {
            result.add(convertSwapPostToMap(post));
        }
        return result;
    }

    @Override
    public Map<String, Object> getSwapPostById(Integer postId) {
        SwapPostVO post = session.get(SwapPostVO.class, postId);
        return post != null ? convertSwapPostToMap(post) : null;
    }

    @Override
    public Map<String, Object> saveSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId) {
        SwapPostVO swapPost = new SwapPostVO();
        LocalDateTime now = LocalDateTime.now();
        swapPost.setPostMemberId(memberId);
        swapPost.setPostTicketId(ticketId);
        swapPost.setPostDescription(description);
        swapPost.setEventId(eventId);
        swapPost.setCreateTime(now);
        swapPost.setUpdateTime(now);
        
        session.persist(swapPost);
        return convertSwapPostToMap(swapPost);
    }

    @Override
    public Map<String, Object> updateSwapPost(Integer postId, String description) {
        SwapPostVO swapPost = session.get(SwapPostVO.class, postId);
        if (swapPost != null) {
            swapPost.setPostDescription(description);
            swapPost.setUpdateTime(LocalDateTime.now());
            session.merge(swapPost);
            return convertSwapPostToMap(swapPost);
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
    public List<Map<String, Object>> listSwapPostsByMemberId(Integer memberId) {
        String hql = "FROM SwapPostVO sp WHERE sp.postMemberId = :memberId ORDER BY sp.createTime DESC";
        List<SwapPostVO> swapPosts = session
                .createQuery(hql, SwapPostVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapPostVO post : swapPosts) {
            result.add(convertSwapPostToMap(post));
        }
        return result;
    }

    @Override
    public Map<String, Object> getSwapPostByTicketId(Integer ticketId) {
        String hql = "FROM SwapPostVO sp WHERE sp.postTicketId = :ticketId";
        List<SwapPostVO> results = session
                .createQuery(hql, SwapPostVO.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
        return results.isEmpty() ? null : convertSwapPostToMap(results.get(0));
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
    public boolean existsSwapPostByTicketId(Integer ticketId) {
        String hql = "SELECT COUNT(sp) FROM SwapPostVO sp WHERE sp.postTicketId = :ticketId";
        Long count = session
                .createQuery(hql, Long.class)
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

    /**
     * 將SwapPostVO物件轉換為Map
     */
    private Map<String, Object> convertSwapPostToMap(SwapPostVO post) {
        Map<String, Object> postInfo = new HashMap<>();
        
        postInfo.put("postId", post.getPostId());
        postInfo.put("postDescription", post.getPostDescription());
        postInfo.put("createTime", post.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 查詢會員資訊
        MemberVO member = getMemberById(post.getPostMemberId());
        if (member != null) {
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickName", member.getNickName());
            postInfo.put("member", memberInfo);
        }
        
        // 查詢活動資訊
        EventInfoVO eventInfo = getEventInfoById(post.getEventId());
        if (eventInfo != null) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", eventInfo.getEventId());
            eventData.put("eventName", eventInfo.getEventName());
            postInfo.put("event", eventData);
        }
        
        // 查詢票券資訊
        BuyerTicketVO ticket = getBuyerTicketById(post.getPostTicketId());
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
            
            postInfo.put("ticket", ticketInfo);
        }
        
        return postInfo;
    }

    private MemberVO getMemberById(Integer memberId) {
        String hql = "FROM MemberVO m WHERE m.memberId = :memberId";
        List<MemberVO> results = session
                .createQuery(hql, MemberVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private EventInfoVO getEventInfoById(Integer eventId) {
        String hql = "FROM EventInfoVO e WHERE e.eventId = :eventId";
        List<EventInfoVO> results = session
                .createQuery(hql, EventInfoVO.class)
                .setParameter("eventId", eventId)
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
    
    @Override
    public Map<String, Object> getMemberByNickname(String nickname) {
        try {
            String hql = "FROM MemberVO m WHERE m.nickName = :nickname AND m.active = 1";
            List<MemberVO> results = session
                    .createQuery(hql, MemberVO.class)
                    .setParameter("nickname", nickname)
                    .setMaxResults(1)
                    .getResultList();
                    
            if (results.isEmpty()) {
                return null;
            }
            
            MemberVO member = results.get(0);
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickname", member.getNickName());
            memberInfo.put("email", member.getEmail());
            memberInfo.put("roleLevel", member.getRoleLevel());
            
            return memberInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 查會員票券
    @Override
    public List<Map<String, Object>> getUserTickets(Integer memberId) {
        String hql = "FROM BuyerTicketVO bt WHERE bt.currentHolderMemberId = :memberId " +
                     "AND bt.used = 0 ORDER BY bt.ticketId DESC";
        
        List<BuyerTicketVO> tickets = session
                .createQuery(hql, BuyerTicketVO.class)
                .setParameter("memberId", memberId)
                .getResultList();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (BuyerTicketVO ticket : tickets) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket.getTicketId());
            ticketInfo.put("participantName", ticket.getParticipantName());
            ticketInfo.put("eventName", ticket.getEventName());
            ticketInfo.put("price", ticket.getPrice());
            
            // 查詢票種資訊
            if (ticket.getTypeId() != null) {
                EventTicketTypeVO ticketType = getEventTicketTypeById(ticket.getTypeId());
                if (ticketType != null) {
                    ticketInfo.put("categoryName", ticketType.getCategoryName());
                }
            }
            
            result.add(ticketInfo);
        }
        
        return result;
    }
}
