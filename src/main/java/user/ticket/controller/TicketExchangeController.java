package user.ticket.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.member.vo.Member;
import user.ticket.service.TicketExchangeService;

/**
 * 票券交換控制器 創建者: archchang 創建日期: 2025-05-26
 */
@RestController
@RequestMapping("/api/ticket-exchange")
public class TicketExchangeController {

	@Autowired
	private TicketExchangeService ticketExchangeService;
	
	/**
     * 依活動ID查詢換票貼文列表
     */
	@GetMapping("/posts/event/{eventId}")
	public ResponseEntity<Map<String, Object>> getSwapPostsByEventId(@PathVariable Integer eventId) {
		try {
			if (eventId == null || eventId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("A0001", "無效的活動ID", "請提供有效的活動ID"));
            }

            List<Map<String, Object>> posts = ticketExchangeService.listSwapPostsByEventId(eventId);
            posts.forEach(this::addPhotoUrlToData);
            
            return ResponseEntity.ok(buildSuccessResponse(posts, posts.size()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(buildErrorResponse("A0001", e.getMessage(), "請提供有效的活動ID"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(buildErrorResponse("B0001", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試"));
		}
	}
	
	/**
     * 創建換票貼文
     */
	@PostMapping("/posts")
	public ResponseEntity<Map<String, Object>> createSwapPost(@RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
			// 從session獲取會員資訊
			Map<String, Object> memberInfo = getMemberFromSession(session);
			if (memberInfo == null) {
				return ResponseEntity.status(401).body(buildErrorResponse("A0001", "未登入或登入已過期", "請重新登入"));
			}

			Integer memberId = (Integer) memberInfo.get("memberId");
            Integer ticketId = (Integer) requestData.get("ticketId");
            String description = (String) requestData.get("description");
            Integer eventId = (Integer) requestData.get("eventId");

			Map<String, Object> data = ticketExchangeService.createSwapPost(memberId, ticketId, description, eventId);
			addPhotoUrlToData(data);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", data);
			response.put("message", "換票貼文創建成功");

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(buildErrorResponse("A0002", e.getMessage(), "請檢查輸入的資料是否正確"));
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
            String userMessage = getUserMessageByError(e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(errorCode, e.getMessage(), userMessage));
		}
	}
	
	 /**
     * 創建換票留言
     */
    @PostMapping("/comments")
    public ResponseEntity<Map<String, Object>> createSwapComment(@RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
			// 從session獲取會員資訊
			Map<String, Object> memberInfo = getMemberFromSession(session);
			if (memberInfo == null) {
				return ResponseEntity.status(401).body(buildErrorResponse("A0001", "未登入或登入已過期", "請重新登入"));
			}

			Integer memberId = (Integer) memberInfo.get("memberId");
            Integer postId = (Integer) requestData.get("postId");
            Integer ticketId = (Integer) requestData.get("ticketId");
            String description = (String) requestData.get("description");

			Map<String, Object> data = ticketExchangeService.createSwapComment(postId, memberId, ticketId, description);
			addPhotoUrlToData(data);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", data);
			response.put("message", "換票留言創建成功");

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(buildErrorResponse("A0003", e.getMessage(), "請檢查輸入的資料是否正確"));
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
            String userMessage = getUserMessageByError(e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(errorCode, e.getMessage(), userMessage));
		}
	}

    /**
     * 查詢貼文的留言列表
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getSwapCommentsByPostId(@PathVariable Integer postId) {
		try {
			if (postId == null || postId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("A0008", "無效的貼文ID", "請提供有效的貼文ID"));
            }

            List<Map<String, Object>> comments = ticketExchangeService.listSwapCommentsByPostId(postId);
            comments.forEach(this::addPhotoUrlToData);
            
            return ResponseEntity.ok(buildSuccessResponse(comments, comments.size()));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(buildErrorResponse("A0008", e.getMessage(), "請提供有效的貼文ID"));
		}
	}

    /**
     * 更新換票留言狀態
     */
    @PutMapping("/comments/{commentId}/status")
    public ResponseEntity<Map<String, Object>> updateSwapCommentStatus(@PathVariable Integer commentId, @RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
			Map<String, Object> memberInfo = getMemberFromSession(session);
            if (memberInfo == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("A0001", "未登入或登入已過期", "請重新登入"));
            }

            Integer memberId = (Integer) memberInfo.get("memberId");
            Integer status = (Integer) requestData.get("status");

            ticketExchangeService.updateSwapCommentStatus(commentId, status, memberId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "留言狀態更新成功");

            return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse("A0009", e.getMessage(), "請提供有效的參數"));
        } catch (RuntimeException e) {
            String errorCode = getErrorCodeFromMessage(e.getMessage());
            String userMessage = getUserMessageByError(e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(errorCode, e.getMessage(), userMessage));
        }
	}
    
    /**
     * 刪除換票貼文
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> removeSwapPost(@PathVariable Integer postId, HttpSession session) {
		try {
			// 從session獲取會員資訊
			Map<String, Object> memberInfo = getMemberFromSession(session);
			if (memberInfo == null) {
				return ResponseEntity.status(401).body(buildErrorResponse("A0001", "未登入或登入已過期", "請重新登入"));
			}

			Integer memberId = (Integer) memberInfo.get("memberId");
            ticketExchangeService.removeSwapPost(postId, memberId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "換票貼文刪除成功");

            return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(buildErrorResponse("A0006", e.getMessage(), "請提供有效的參數"));
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
            String userMessage = getUserMessageByError(e.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(errorCode, e.getMessage(), userMessage));
		}
	}
    
    /**
     * 建立成功回應
     */
    private Map<String, Object> buildSuccessResponse(Object data, Integer total) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        if (total != null) {
            response.put("total", total);
        }
        return response;
    }

    /**
     * 建立錯誤回應
     */
    private Map<String, Object> buildErrorResponse(String errorCode, String errorMessage, String userMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("errorMessage", errorMessage);
        errorResponse.put("userMessage", userMessage);
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return errorResponse;
    }

    /**
     * 從錯誤訊息獲取錯誤代碼
     */
    private String getErrorCodeFromMessage(String message) {
        if (message.contains("已發布換票貼文")) {
            return "E0001";
        } else if (message.contains("已用於換票留言")) {
            return "E0003";
        } else if (message.contains("找不到換票貼文")) {
            return "E0011";
        } else if (message.contains("權限不足")) {
            return "E0012";
        }
        return "E0000";
    }

    /**
     * 從錯誤訊息獲取用戶友好訊息
     */
    private String getUserMessageByError(String errorMessage) {
        if (errorMessage.contains("已發布換票貼文")) {
            return "此票券已經發布過換票貼文，請選擇其他票券";
        } else if (errorMessage.contains("已用於換票留言")) {
            return "此票券已經用於換票留言，請選擇其他票券";
        } else if (errorMessage.contains("找不到換票貼文")) {
            return "找不到該換票貼文";
        } else if (errorMessage.contains("權限不足")) {
            return "您沒有權限進行此操作";
        }
        return "操作失敗，請稍後再試";
    }

    /**
     * 為資料添加照片URL
     */
    @SuppressWarnings("unchecked")
    private void addPhotoUrlToData(Map<String, Object> data) {
        Map<String, Object> member = (Map<String, Object>) data.get("member");
        if (member != null && member.get("memberId") != null) {
            Integer memberId = (Integer) member.get("memberId");
            member.put("photoUrl", "/api/member-photos/" + memberId);
        }
    }

    /**
     * 從session獲取會員資訊
     */
    private Map<String, Object> getMemberFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }

        Member member = (Member) session.getAttribute("member");
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
}