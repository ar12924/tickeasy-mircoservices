package user.ticket.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.ticket.dao.SwapPostDao;
import user.ticket.dao.SwapCommentDao;
import user.ticket.service.TicketExchangeService;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventInfoVO;
import user.ticket.vo.EventTicketTypeVO;
import user.ticket.vo.MemberVO;
import user.ticket.vo.SwapCommentVO;
import user.ticket.vo.SwapPostVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * 票券交換服務實作類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
@Service
@Transactional
public class TicketExchangeServiceImpl implements TicketExchangeService {

    @Autowired
    private SwapPostDao swapPostDao;
    
    @Autowired
    private SwapCommentDao swapCommentDao;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listSwapPostsByEventId(Integer eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("活動ID不能為空或小於等於0");
        }

        List<SwapPostVO> posts = swapPostDao.listSwapPostsByEventId(eventId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapPostVO post : posts) {
            Map<String, Object> postInfo = convertSwapPostToMap(post);
            enrichPostData(postInfo);
            result.add(postInfo);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> createSwapPost(Integer memberId, Integer ticketId, String description, Integer eventId) {
        // 參數驗證
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }
        if (ticketId == null || ticketId <= 0) {
            throw new IllegalArgumentException("票券ID不能為空或小於等於0");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("貼文描述不能為空");
        }
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("活動ID不能為空或小於等於0");
        }

        // 業務規則檢查
        if (swapPostDao.existsSwapPostByTicketId(ticketId)) {
            throw new RuntimeException("此票券已發布換票貼文");
        }
        
        SwapPostVO savedPost = swapPostDao.saveSwapPost(memberId, ticketId, description, eventId);
        Map<String, Object> postInfo = convertSwapPostToMap(savedPost);
        enrichPostData(postInfo);
        
        return postInfo;
    }

    @Override
    public Map<String, Object> createSwapComment(Integer postId, Integer memberId, Integer ticketId, String description) {
        // 參數驗證
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("貼文ID不能為空或小於等於0");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }
        if (ticketId == null || ticketId <= 0) {
            throw new IllegalArgumentException("票券ID不能為空或小於等於0");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("留言描述不能為空");
        }

        // 業務規則檢查
        if (swapCommentDao.existsSwapCommentByTicketId(ticketId)) {
            throw new RuntimeException("此票券已用於換票留言");
        }
        
        SwapCommentVO savedComment = swapCommentDao.saveSwapComment(postId, memberId, ticketId, description);
        Map<String, Object> commentInfo = convertSwapCommentToMap(savedComment);
        enrichCommentData(commentInfo);
        
        return commentInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMemberSwapPosts(Integer memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        List<SwapPostVO> posts = swapPostDao.listSwapPostsByMemberId(memberId);
        
        // 為每個貼文添加額外資訊
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapPostVO post : posts) {
            Map<String, Object> postInfo = convertSwapPostToMap(post);
            enrichPostData(postInfo);
            result.add(postInfo);
        }
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMemberSwapComments(Integer memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        List<SwapCommentVO> comments = swapCommentDao.listSwapCommentsByMemberId(memberId);
        
        // 為每個留言添加額外資訊
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapCommentVO comment : comments) {
            Map<String, Object> commentInfo = convertSwapCommentToMap(comment);
            enrichCommentData(commentInfo);
            result.add(commentInfo);
        }
        
        return result;
    }

    @Override
    public void removeSwapPost(Integer postId, Integer memberId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("貼文ID不能為空或小於等於0");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        SwapPostVO post = swapPostDao.getSwapPostById(postId);
        if (post == null) {
            throw new RuntimeException("找不到換票貼文");
        }
        
        if (!swapPostDao.isPostOwner(postId, memberId)) {
            throw new RuntimeException("權限不足，只能刪除自己的貼文");
        }
        
        swapPostDao.removeSwapPost(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listSwapCommentsByPostId(Integer postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("貼文ID不能為空或小於等於0");
        }

        List<SwapCommentVO> comments = swapCommentDao.listSwapCommentsByPostId(postId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SwapCommentVO comment : comments) {
        	Map<String, Object> commentInfo = convertSwapCommentToMap(comment);
            enrichCommentData(commentInfo);
            result.add(commentInfo);
        }
        
        return result;
    }

    @Override
    public void updateSwapCommentStatus(Integer commentId, Integer status, Integer memberId) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("留言ID不能為空或小於等於0");
        }
        if (status == null || status < 0 || status > 3) {
            throw new IllegalArgumentException("狀態值無效，必須為0-3之間");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        // 檢查權限（留言者或貼文擁有者可以更新狀態）
        boolean hasPermission = swapCommentDao.hasCommentPermission(commentId, memberId) || 
                               swapCommentDao.isPostOwnerByCommentId(commentId, memberId);
        
        if (!hasPermission) {
            throw new RuntimeException("權限不足，無法更新留言狀態");
        }
        
        boolean updated = swapCommentDao.updateSwapCommentStatus(commentId, status);
        
        if (!updated) {
            throw new RuntimeException("留言狀態更新失敗");
        }
    }

    /**
     * 豐富貼文資料，添加額外資訊
     */
//    @SuppressWarnings("unchecked")
    private void enrichPostData(Map<String, Object> post) {
        // 添加相對時間顯示
        String createTime = (String) post.get("createTime");
        if (createTime != null) {
            post.put("relativeTime", calculateRelativeTime(createTime));
        }
        
        // 添加貼文狀態
        post.put("status", "轉票進行中");
        
        // 確保所有需要的字段都存在
        if (!post.containsKey("member")) {
            post.put("member", new HashMap<String, Object>());
        }
        if (!post.containsKey("ticket")) {
            post.put("ticket", new HashMap<String, Object>());
        }
        if (!post.containsKey("event")) {
            post.put("event", new HashMap<String, Object>());
        }
    }

    /**
     * 豐富留言資料，添加額外資訊
     */
//    @SuppressWarnings("unchecked")
    private void enrichCommentData(Map<String, Object> comment) {
        // 添加相對時間顯示
        String createTime = (String) comment.get("createTime");
        if (createTime != null) {
            comment.put("relativeTime", calculateRelativeTime(createTime));
        }
        
        // 添加狀態文字描述
        Integer status = (Integer) comment.get("swappedStatus");
        if (status != null) {
            String statusText = getStatusText(status);
            comment.put("statusText", statusText);
        }
        
        // 確保所有需要的字段都存在
        if (!comment.containsKey("member")) {
            comment.put("member", new HashMap<String, Object>());
        }
        if (!comment.containsKey("ticket")) {
            comment.put("ticket", new HashMap<String, Object>());
        }
    }

    /**
     * 計算相對時間
     */
    private String calculateRelativeTime(String createTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createTime = LocalDateTime.parse(createTimeStr, formatter);
            LocalDateTime now = LocalDateTime.now();
            
            long hours = java.time.Duration.between(createTime, now).toHours();
            long days = java.time.Duration.between(createTime, now).toDays();
            
            if (hours < 1) {
                return "剛剛";
            } else if (hours < 24) {
                return hours + "小時前";
            } else {
                return days + "天前";
            }
        } catch (Exception e) {
            return "未知時間";
        }
    }

    /**
     * 獲取狀態文字描述
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "待換票";
            case 1:
                return "待確認";
            case 2:
                return "已完成";
            case 3:
                return "已取消";
            default:
                return "未知狀態";
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMemberByNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("會員暱稱不能為空");
        }
        
        MemberVO member = swapPostDao.getMemberByNickname(nickname.trim());
        if (member == null) {
            return null;
        }
        
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("memberId", member.getMemberId());
        memberInfo.put("nickname", member.getNickName());
        memberInfo.put("email", member.getEmail());
        memberInfo.put("roleLevel", member.getRoleLevel());
        
        return memberInfo;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserTicketsByNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("會員暱稱不能為空");
        }
        
        // 先獲取會員資訊
        Map<String, Object> memberInfo = getMemberByNickname(nickname);
        if (memberInfo == null) {
            return new ArrayList<>();
        }
        
        Integer memberId = (Integer) memberInfo.get("memberId");
        
        // 查詢該會員的票券
        List<BuyerTicketVO> tickets = swapPostDao.getUserTickets(memberId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (BuyerTicketVO ticket : tickets) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket.getTicketId());
            ticketInfo.put("participantName", ticket.getParticipantName());
            ticketInfo.put("eventName", ticket.getEventName());
            ticketInfo.put("price", ticket.getPrice());
            
            // 查詢票種資訊
            if (ticket.getTypeId() != null) {
                EventTicketTypeVO ticketType = swapPostDao.getEventTicketTypeById(ticket.getTypeId());
                if (ticketType != null) {
                    ticketInfo.put("categoryName", ticketType.getCategoryName());
                }
            }
            
            result.add(ticketInfo);
        }
        
        return result;
    }
    
    /**
     * 將SwapPostVO物件轉換為Map - 業務邏輯（從 DAO 層移過來）
     */
    private Map<String, Object> convertSwapPostToMap(SwapPostVO post) {
        Map<String, Object> postInfo = new HashMap<>();
        
        postInfo.put("postId", post.getPostId());
        postInfo.put("postDescription", post.getPostDescription());
        postInfo.put("createTime", post.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 查詢會員資訊
        MemberVO member = swapPostDao.getMemberById(post.getPostMemberId());
        if (member != null) {
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickName", member.getNickName());
            postInfo.put("member", memberInfo);
        }
        
        // 查詢活動資訊
        EventInfoVO eventInfo = swapPostDao.getEventInfoById(post.getEventId());
        if (eventInfo != null) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", eventInfo.getEventId());
            eventData.put("eventName", eventInfo.getEventName());
            postInfo.put("event", eventData);
        }
        
        // 查詢票券資訊
        BuyerTicketVO ticket = swapPostDao.getBuyerTicketById(post.getPostTicketId());
        if (ticket != null) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket.getTicketId());
            ticketInfo.put("participantName", ticket.getParticipantName());
            ticketInfo.put("eventName", ticket.getEventName());
            
            // 查詢票種資訊
            if (ticket.getTypeId() != null) {
                EventTicketTypeVO ticketType = swapPostDao.getEventTicketTypeById(ticket.getTypeId());
                if (ticketType != null) {
                    ticketInfo.put("categoryName", ticketType.getCategoryName());
                    ticketInfo.put("price", ticketType.getPrice());
                }
            }
            
            postInfo.put("ticket", ticketInfo);
        }
        
        return postInfo;
    }

    /**
     * 將SwapCommentVO物件轉換為Map - 業務邏輯（從 DAO 層移過來）
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
        MemberVO member = swapCommentDao.getMemberById(comment.getCommentMemberId());
        if (member != null) {
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberId", member.getMemberId());
            memberInfo.put("nickName", member.getNickName());
            commentInfo.put("member", memberInfo);
        }
        
        // 查詢票券資訊
        BuyerTicketVO ticket = swapCommentDao.getBuyerTicketById(comment.getCommentTicketId());
        if (ticket != null) {
            Map<String, Object> ticketInfo = new HashMap<>();
            ticketInfo.put("ticketId", ticket.getTicketId());
            ticketInfo.put("participantName", ticket.getParticipantName());
            ticketInfo.put("eventName", ticket.getEventName());
            
            // 查詢票種資訊
            if (ticket.getTypeId() != null) {
                EventTicketTypeVO ticketType = swapCommentDao.getEventTicketTypeById(ticket.getTypeId());
                if (ticketType != null) {
                    ticketInfo.put("categoryName", ticketType.getCategoryName());
                    ticketInfo.put("price", ticketType.getPrice());
                }
            }
            
            commentInfo.put("ticket", ticketInfo);
        }
        
        return commentInfo;
    }
}
