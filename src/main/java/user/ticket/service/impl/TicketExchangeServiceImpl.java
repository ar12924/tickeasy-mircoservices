package user.ticket.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user.ticket.dao.SwapPostDao;
import user.ticket.dao.SwapCommentDao;
import user.ticket.service.TicketExchangeService;

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

        List<Map<String, Object>> posts = swapPostDao.listSwapPostsByEventId(eventId);
        
        // 為每個貼文添加額外資訊
        for (Map<String, Object> post : posts) {
            enrichPostData(post);
        }
        
        return posts;
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
        
        Map<String, Object> savedPost = swapPostDao.saveSwapPost(memberId, ticketId, description, eventId);
        enrichPostData(savedPost);
        
        return savedPost;
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
        
        Map<String, Object> savedComment = swapCommentDao.saveSwapComment(postId, memberId, ticketId, description);
        enrichCommentData(savedComment);
        
        return savedComment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMemberSwapPosts(Integer memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        List<Map<String, Object>> posts = swapPostDao.listSwapPostsByMemberId(memberId);
        
        // 為每個貼文添加額外資訊
        for (Map<String, Object> post : posts) {
            enrichPostData(post);
        }
        
        return posts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listMemberSwapComments(Integer memberId) {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        List<Map<String, Object>> comments = swapCommentDao.listSwapCommentsByMemberId(memberId);
        
        // 為每個留言添加額外資訊
        for (Map<String, Object> comment : comments) {
            enrichCommentData(comment);
        }
        
        return comments;
    }

    @Override
    public void removeSwapPost(Integer postId, Integer memberId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("貼文ID不能為空或小於等於0");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("會員ID不能為空或小於等於0");
        }

        Map<String, Object> post = swapPostDao.getSwapPostById(postId);
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

        List<Map<String, Object>> comments = swapCommentDao.listSwapCommentsByPostId(postId);
        
        // 為每個留言添加額外資訊
        for (Map<String, Object> comment : comments) {
            enrichCommentData(comment);
        }
        
        return comments;
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
        
        try {
            // 統一使用 swapPostDao 來查詢會員資訊
            return swapPostDao.getMemberByNickname(nickname.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
