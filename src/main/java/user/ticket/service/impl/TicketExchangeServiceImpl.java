package user.ticket.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.member.vo.Member;
import user.ticket.dao.BuyerTicketDao;
import user.ticket.dao.SwapCommentDao;
import user.ticket.dao.SwapPostDao;
import user.ticket.service.TicketExchangeService;
import user.ticket.vo.BuyerTicketVO;
import user.ticket.vo.EventTicketTypeVO;
import user.ticket.vo.SwapCommentVO;
import user.ticket.vo.SwapPostVO;

/**
 * 票券交換服務實作類 創建者: archchang 創建日期: 2025-05-26
 */
@Service
@Transactional
public class TicketExchangeServiceImpl implements TicketExchangeService {

	@Autowired
	private SwapPostDao swapPostDao;

	@Autowired
	private SwapCommentDao swapCommentDao;

	@Autowired
	private BuyerTicketDao buyerTicketDao;

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> listSwapPostsByEventId(Integer eventId) {
		if (eventId == null || eventId <= 0) {
			throw new IllegalArgumentException("活動ID不能為空或小於等於0");
		}

		List<Map<String, Object>> posts = swapPostDao.listSwapPostsWithDetailsByEventId(eventId);

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

		// 🔧 修正：業務規則檢查，傳入 memberId
		validateTicketOwnershipAndAvailability(ticketId, memberId);

		SwapPostVO savedPost = swapPostDao.saveSwapPost(memberId, ticketId, description, eventId);

		// 創建後立即查詢完整資訊
		List<Map<String, Object>> posts = swapPostDao.listSwapPostsWithDetailsByEventId(eventId);
		Map<String, Object> postInfo = null;

		for (Map<String, Object> post : posts) {
			Object postIdObj = post.get("postId");
			if (postIdObj != null && postIdObj.equals(savedPost.getPostId())) {
				postInfo = post;
				break;
			}
		}

		if (postInfo != null) {
			enrichPostData(postInfo);
		}

		return postInfo;
	}

	@Override
	public Map<String, Object> createSwapComment(Integer postId, Integer memberId, Integer ticketId,
			String description) {
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

		// 🔧 修正：業務規則檢查，傳入 memberId
		validateTicketOwnershipAndAvailability(ticketId, memberId);

		// 同活動驗證
		SwapPostVO post = swapPostDao.getSwapPostById(postId);
		if (post == null) {
			throw new RuntimeException("找不到換票貼文");
		}
		validateSameEvent(post.getPostTicketId(), ticketId);

		// 保存留言
		SwapCommentVO savedComment = swapCommentDao.saveSwapComment(postId, memberId, ticketId, description);

		// 構建返回資料
		Map<String, Object> commentInfo = new HashMap<>();
		commentInfo.put("commentId", savedComment.getCommentId());
		commentInfo.put("commentDescription", savedComment.getCommentDescription());
		commentInfo.put("swappedStatus", savedComment.getSwappedStatus());
		commentInfo.put("createTime", savedComment.getCreateTime());
		commentInfo.put("updateTime", savedComment.getUpdateTime());

		// 添加會員資訊
		Map<String, Object> memberInfo = new HashMap<>();
		Member member = swapCommentDao.getMemberById(memberId);
		if (member != null) {
			memberInfo.put("memberId", member.getMemberId());
			memberInfo.put("nickName", member.getNickName());
			memberInfo.put("photoUrl", "/api/member-photos/" + member.getMemberId());
		}
		commentInfo.put("member", memberInfo);

		// 添加票券資訊
		Map<String, Object> ticketInfo = new HashMap<>();
		BuyerTicketVO ticket = swapCommentDao.getBuyerTicketById(ticketId);
		if (ticket != null) {
			ticketInfo.put("ticketId", ticket.getTicketId());
			ticketInfo.put("participantName", ticket.getParticipantName());
			ticketInfo.put("price", ticket.getPrice());

			if (ticket.getTypeId() != null) {
				EventTicketTypeVO ticketType = swapCommentDao.getEventTicketTypeById(ticket.getTypeId());
				if (ticketType != null) {
					ticketInfo.put("categoryName", ticketType.getCategoryName());
				}
			}
		}
		commentInfo.put("ticket", ticketInfo);

		// 添加額外資訊
		enrichCommentData(commentInfo);

		return commentInfo;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> listMemberSwapPosts(Integer memberId) {
		if (memberId == null || memberId <= 0) {
			throw new IllegalArgumentException("會員ID不能為空或小於等於0");
		}

		List<Map<String, Object>> posts = swapPostDao.listSwapPostsWithDetailsByMemberId(memberId);

		// 添加相對時間和狀態資訊
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

		List<Map<String, Object>> comments = swapCommentDao.listSwapCommentsWithDetailsByMemberId(memberId);

		// 添加相對時間和狀態資訊
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

		List<Map<String, Object>> comments = swapCommentDao.listSwapCommentsWithDetailsByPostId(postId);

		// 添加相對時間和狀態資訊
		for (Map<String, Object> comment : comments) {
			enrichCommentData(comment);
		}

		return comments;
	}

	// 🔧 核心修改：簡化狀態更新邏輯
	@Override
	public void updateSwapCommentStatus(Integer commentId, Integer status, Integer memberId) {
		System.out.println("===== 開始更新狀態 =====");
	    System.out.println("commentId: " + commentId + ", 目標狀態: " + status + ", memberId: " + memberId);
	    
	    validateCommentExists(commentId);
	    
	    SwapCommentVO comment = swapCommentDao.getSwapCommentById(commentId);
	    System.out.println("當前狀態: " + comment.getSwappedStatus());
	    
	    SwapPostVO post = swapPostDao.getPostByCommentId(commentId);
	    if (post == null) {
	        throw new RuntimeException("找不到相關的貼文資訊");
	    }
	    
	    // 🔧 簡化：只處理兩種狀態轉換
	    switch (status) {
	    case 2: // 🔧 修改：接受請求直接完成交換 (0→2)
	        handleAcceptAndCompleteExchange(comment, post, memberId);
	        break;
	    case 3: // 取消請求 (0→3)
	        handleCancelRequest(comment, post, memberId);
	        break;
	    default:
	        throw new RuntimeException("無效的狀態值: " + status);
	    }
	    
	    System.out.println("準備更新狀態到資料庫...");
	    
	    // 更新狀態
	    boolean updated = swapCommentDao.updateSwapCommentStatus(commentId, status);
	    System.out.println("更新結果: " + updated);
	    
	    if (!updated) {
	        throw new RuntimeException("更新狀態失敗");
	    }
	    
	    // 驗證更新是否成功
	    SwapCommentVO updatedComment = swapCommentDao.getSwapCommentById(commentId);
	    System.out.println("更新後狀態: " + updatedComment.getSwappedStatus());
	    System.out.println("===== 更新狀態完成 =====");
	}

	// 🔧 新增：接受請求並直接完成交換
	private void handleAcceptAndCompleteExchange(SwapCommentVO comment, SwapPostVO post, Integer memberId) {
		// 權限檢查：只有貼文發起方可以接受並完成交換
		if (!post.getPostMemberId().equals(memberId)) {
			throw new RuntimeException("權限不足，只有貼文發起方可以接受換票請求");
		}

		// 🔧 修改：狀態檢查改為 0→2
		if (comment.getSwappedStatus() != 0) {
			throw new RuntimeException("只能接受待換票狀態的請求");
		}

		// 🔧 檢查是否已有其他已完成的留言
		List<SwapCommentVO> existingComments = swapCommentDao.listSwapCommentsByPostId(post.getPostId());
		boolean hasCompletedComment = existingComments.stream()
				.anyMatch(c -> !c.getCommentId().equals(comment.getCommentId()) && c.getSwappedStatus() == 2);

		if (hasCompletedComment) {
			throw new RuntimeException("此貼文已有完成的交換，無法重複交換");
		}

		// 驗證票券可用性（執行前最後檢查）
		validateTicketBeforeExchange(post.getPostTicketId(), post.getPostMemberId());
		validateTicketBeforeExchange(comment.getCommentTicketId(), comment.getCommentMemberId());

		// 🔧 關鍵：直接執行票券交換
		executeTicketExchange(post.getPostTicketId(), comment.getCommentTicketId(), post.getPostMemberId(),
				comment.getCommentMemberId());

		System.out.println("接受請求並完成交換邏輯執行完成");
	}

	// 🔧 修正：處理取消請求邏輯
	private void handleCancelRequest(SwapCommentVO comment, SwapPostVO post, Integer memberId) {
		// 權限檢查：留言發起方、貼文發起方都可以取消
		if (!comment.getCommentMemberId().equals(memberId) && !post.getPostMemberId().equals(memberId)) {
			throw new RuntimeException("權限不足，只有交換雙方可以取消請求");
		}

		// 🔧 修改：狀態檢查，只能取消待換票狀態
		if (comment.getSwappedStatus() != 0) {
			throw new RuntimeException("只能取消待換票狀態的請求");
		}
	}

	/**
	 * 豐富貼文資料，添加額外資訊
	 */
//    @SuppressWarnings("unchecked")
	private void enrichPostData(Map<String, Object> post) {
		// 添加相對時間顯示
		Object createTimeObj = post.get("createTime");
		if (createTimeObj != null) {
			String createTimeStr;
			if (createTimeObj instanceof Timestamp) {
				createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp) createTimeObj);
			} else {
				createTimeStr = createTimeObj.toString();
			}
			post.put("createTime", createTimeStr);
			post.put("relativeTime", calculateRelativeTime(createTimeStr));
		}

		// 添加貼文狀態
		String status = determinePostStatus(post);
		post.put("status", status);
	}

	/**
	 * 豐富留言資料，添加額外資訊
	 */
//    @SuppressWarnings("unchecked")
	private void enrichCommentData(Map<String, Object> comment) {
		// 添加相對時間顯示
		Object createTimeObj = comment.get("createTime");
		if (createTimeObj != null) {
			String createTimeStr;
			if (createTimeObj instanceof Timestamp) {
				createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp) createTimeObj);
			} else {
				createTimeStr = createTimeObj.toString();
			}
			comment.put("createTime", createTimeStr);
			comment.put("relativeTime", calculateRelativeTime(createTimeStr));
		}

		// 添加狀態文字描述
		Object statusObj = comment.get("swappedStatus");
		if (statusObj != null) {
			Integer status = convertToInteger(statusObj);
			if (status != null) {
				String statusText = getStatusText(status);
				comment.put("statusText", statusText);
			}
		}
	}

	/**
	 * 計算相對時間
	 */
	private String calculateRelativeTime(String createTimeStr) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date createTime = formatter.parse(createTimeStr);
			Date now = new Date();

			long diffInMillis = now.getTime() - createTime.getTime();
			long hours = diffInMillis / (60 * 60 * 1000);
			long days = diffInMillis / (24 * 60 * 60 * 1000);

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
	 * 🔧 修改：獲取狀態文字描述（簡化版）
	 */
	private String getStatusText(Integer status) {
		switch (status) {
		case 0:
            return "待換票";
        case 2:
            return "已完成";
        case 3:
            return "已取消";
        default:
            return "未知狀態";
		}
	}

	/**
	 * 檢查票券是否屬於會員
	 */
	private boolean isTicketOwnedByMember(Integer ticketId, Integer memberId) {
		return buyerTicketDao.checkTicketOwnership(ticketId, memberId);
	}

	/**
	 * 檢查票券是否可用（存在且未使用）
	 */
	private boolean isTicketAvailable(Integer ticketId) {
		if (!buyerTicketDao.ticketExists(ticketId)) {
			return false;
		}

		Integer usedStatus = buyerTicketDao.getTicketUsedStatus(ticketId);
		return usedStatus != null && usedStatus == 0;
	}

	/**
	 * 檢查票券是否已在其他轉票中
	 */
	private boolean isTicketAlreadyInExchange(Integer ticketId, Integer memberId) {
		// 1. 檢查是否已在當前用戶的貼文中
		SwapPostVO existingPost = swapPostDao.getSwapPostByTicketId(ticketId);
		if (existingPost != null && existingPost.getPostMemberId().equals(memberId)) {
			return true; // 該票券已在用戶的貼文中使用
		}

		// 2. 檢查是否已在當前用戶的進行中留言中（狀態為0）
		List<SwapCommentVO> comments = swapCommentDao.findCommentsByTicketId(ticketId);
		for (SwapCommentVO comment : comments) {
			// 🔧 關鍵修正：只檢查當前用戶的進行中留言
			if (comment.getCommentMemberId().equals(memberId) && comment.getSwappedStatus() == 0) {
				return true; // 該票券正在用戶的進行中留言中使用
			}
		}

		return false; // 票券可以使用
	}

	/**
	 * 驗證票券擁有權和可用性
	 */
	private void validateTicketOwnershipAndAvailability(Integer ticketId, Integer memberId) {
		if (!isTicketOwnedByMember(ticketId, memberId)) {
	        throw new RuntimeException("票券不屬於該會員");
	    }

	    if (!isTicketAvailable(ticketId)) {
	        throw new RuntimeException("票券已被使用或不可用");
	    }

	    // 🔧 修正：傳入 memberId 參數
	    if (isTicketAlreadyInExchange(ticketId, memberId)) {
	        throw new RuntimeException("您已將此票券用於其他進行中的換票");
	    }
	}

	/**
	 * 轉票執行前的最終驗證
	 */
	private void validateTicketBeforeExchange(Integer ticketId, Integer memberId) {
		if (!buyerTicketDao.checkTicketOwnership(ticketId, memberId)) {
			throw new RuntimeException("票券擁有者已變更，無法執行轉移");
		}

		Integer usedStatus = buyerTicketDao.getTicketUsedStatus(ticketId);
		if (usedStatus == null || usedStatus != 0) {
			throw new RuntimeException("票券已被使用，無法執行轉移");
		}
	}

	/**
	 * 驗證兩張票券是否屬於同一活動
	 */
	private void validateSameEvent(Integer postTicketId, Integer commentTicketId) {
		Integer postEventId = buyerTicketDao.getTicketEventId(postTicketId);
		Integer commentEventId = buyerTicketDao.getTicketEventId(commentTicketId);

		if (postEventId == null || commentEventId == null) {
			throw new RuntimeException("無法確認票券對應的活動");
		}

		if (!postEventId.equals(commentEventId)) {
			throw new RuntimeException("只能交換同一活動的票券");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getUserTickets(Integer memberId) {
		if (memberId == null || memberId <= 0) {
			throw new IllegalArgumentException("會員ID不能為空或小於等於0");
		}

		List<BuyerTicketVO> tickets = buyerTicketDao.getTicketsByMemberId(memberId);
		List<Map<String, Object>> result = new ArrayList<>();

		for (BuyerTicketVO ticket : tickets) {
			Map<String, Object> ticketInfo = new HashMap<>();
			ticketInfo.put("ticketId", ticket.getTicketId());
			ticketInfo.put("participantName", ticket.getParticipantName());
			ticketInfo.put("eventName", ticket.getEventName());
			ticketInfo.put("price", ticket.getPrice());

			ticketInfo.put("createTime", ticket.getCreateTime());
			ticketInfo.put("orderId", ticket.getOrderId());

			if (ticket.getTypeId() != null) {
				try {
					EventTicketTypeVO ticketType = swapPostDao.getEventTicketTypeById(ticket.getTypeId());
					if (ticketType != null) {
						ticketInfo.put("categoryName", ticketType.getCategoryName());
					}
				} catch (Exception e) {
					ticketInfo.put("categoryName", "未知票種");
				}
			}

			result.add(ticketInfo);
		}

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getUserTicketsByEvent(Integer memberId, Integer eventId) {
		if (memberId == null || memberId <= 0) {
			throw new IllegalArgumentException("會員ID不能為空或小於等於0");
		}
		if (eventId == null || eventId <= 0) {
			throw new IllegalArgumentException("活動ID不能為空或小於等於0");
		}

		List<BuyerTicketVO> tickets = buyerTicketDao.getTicketsByMemberIdAndEventId(memberId, eventId);
		List<Map<String, Object>> result = new ArrayList<>();

		for (BuyerTicketVO ticket : tickets) {
			Map<String, Object> ticketInfo = new HashMap<>();
			ticketInfo.put("ticketId", ticket.getTicketId());
			ticketInfo.put("participantName", ticket.getParticipantName());
			ticketInfo.put("eventName", ticket.getEventName());
			ticketInfo.put("price", ticket.getPrice());

			ticketInfo.put("createTime", ticket.getCreateTime());
			ticketInfo.put("orderId", ticket.getOrderId());

			if (ticket.getTypeId() != null) {
				try {
					EventTicketTypeVO ticketType = swapPostDao.getEventTicketTypeById(ticket.getTypeId());
					if (ticketType != null) {
						ticketInfo.put("categoryName", ticketType.getCategoryName());
					}
				} catch (Exception e) {
					ticketInfo.put("categoryName", "未知票種");
				}
			}

			result.add(ticketInfo);
		}

		return result;
	}

	private Integer convertToInteger(Object value) {
		if (value == null) {
			return null;
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

		if (value instanceof Boolean) {
			return ((Boolean) value) ? 1 : 0;
		}

		if (value instanceof String) {
			try {
				return Integer.valueOf((String) value);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		return null;
	}

	private String determinePostStatus(Map<String, Object> post) {
		Object postIdObj = post.get("postId");
		if (postIdObj == null) {
			return "轉票進行中"; // 預設狀態
		}

		Integer postId = convertToInteger(postIdObj);
		if (postId == null || postId <= 0) {
			return "轉票進行中";
		}

		try {
			// 🔧 使用你現有的DAO方法
			List<SwapCommentVO> comments = swapCommentDao.listSwapCommentsByPostId(postId);

			if (comments != null && !comments.isEmpty()) {
				// 🔧 修改：檢查是否有已完成(status=2)的留言
				boolean hasCompletedComment = comments.stream()
						.anyMatch(comment -> comment.getSwappedStatus() != null && comment.getSwappedStatus() == 2);

				if (hasCompletedComment) {
					return "轉票已完成";
				}
			}

			return "轉票進行中";

		} catch (Exception e) {
			// 發生錯誤時回傳預設狀態，避免整個貼文載入失敗
			System.err.println("判斷貼文狀態時發生錯誤，postId: " + postId + ", error: " + e.getMessage());
			return "轉票進行中";
		}
	}

	/**
	 * 🔧 執行票券交換
	 */
	@Transactional
	private void executeTicketExchange(Integer postTicketId, Integer commentTicketId, Integer postMemberId,
			Integer commentMemberId) {
		System.out.println("===== 開始執行票券交換 =====");
	    System.out.println("貼文票券: " + postTicketId + " → 會員: " + commentMemberId);
	    System.out.println("留言票券: " + commentTicketId + " → 會員: " + postMemberId);
	    
	    try {
	        // 執行票券轉移
	        boolean post2Comment = buyerTicketDao.updateTicketOwner(postTicketId, commentMemberId);
	        System.out.println("貼文票券轉移結果: " + post2Comment);
	        
	        if (!post2Comment) {
	            throw new RuntimeException("貼文票券轉移失敗");
	        }
	        
	        boolean comment2Post = buyerTicketDao.updateTicketOwner(commentTicketId, postMemberId);
	        System.out.println("留言票券轉移結果: " + comment2Post);
	        
	        if (!comment2Post) {
	            // 第二步失敗時回滾第一步
	            buyerTicketDao.updateTicketOwner(postTicketId, postMemberId);
	            throw new RuntimeException("留言票券轉移失敗，已回滾貼文票券");
	        }
	        
	        System.out.println("===== 票券交換完成 =====");
	    } catch (Exception e) {
	        System.err.println("票券交換失敗: " + e.getMessage());
	        e.printStackTrace();
	        // 確保交易失敗時的資料一致性
	        throw new RuntimeException("票券交換失敗：" + e.getMessage());
	    }
	}
	
	/**
     * 🔧 驗證留言是否存在
     */
    private void validateCommentExists(Integer commentId) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("留言ID不能為空或小於等於0");
        }
        
        SwapCommentVO comment = swapCommentDao.getSwapCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("找不到指定的留言");
        }
    }
}