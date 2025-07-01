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
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的活動ID"));
            }

            List<Map<String, Object>> posts = ticketExchangeService.listSwapPostsByEventId(eventId);
            addPhotoUrls(posts);

            return ResponseEntity.ok(buildSuccessResponse(posts, posts.size()));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}
	
	/**
     * 創建換票貼文
     */
	@PostMapping("/posts")
	public ResponseEntity<Map<String, Object>> createSwapPost(@RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            Integer ticketId = (Integer) requestData.get("ticketId");
            String description = (String) requestData.get("description");
            Integer eventId = (Integer) requestData.get("eventId");

            Map<String, Object> data = ticketExchangeService.createSwapPost(memberId, ticketId, description, eventId);
            addPhotoUrl(data);

            return ResponseEntity.ok(buildSuccessResponse(data, "換票貼文創建成功"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}
	
	 /**
     * 創建換票留言
     */
    @PostMapping("/comments")
    public ResponseEntity<Map<String, Object>> createSwapComment(@RequestBody Map<String, Object> requestData, HttpSession session) {
    	try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            Integer postId = (Integer) requestData.get("postId");
            Integer ticketId = (Integer) requestData.get("ticketId");
            String description = (String) requestData.get("description");

            Map<String, Object> data = ticketExchangeService.createSwapComment(postId, memberId, ticketId, description);
            addPhotoUrl(data);

            return ResponseEntity.ok(buildSuccessResponse(data, "換票留言創建成功"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}

    /**
     * 查詢貼文的留言列表
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getSwapCommentsByPostId(@PathVariable Integer postId) {
    	try {
            if (postId == null || postId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的貼文ID"));
            }

            List<Map<String, Object>> comments = ticketExchangeService.listSwapCommentsByPostId(postId);
            addPhotoUrls(comments);

            return ResponseEntity.ok(buildSuccessResponse(comments, comments.size()));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}

    /**
     * 更新換票留言狀態
     */
    @PutMapping("/comments/{commentId}/status")
    public ResponseEntity<Map<String, Object>> updateSwapCommentStatus(@PathVariable Integer commentId, @RequestBody Map<String, Object> requestData, HttpSession session) {
    	try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            Integer status = (Integer) requestData.get("status");
            ticketExchangeService.updateSwapCommentStatus(commentId, status, memberId);

            return ResponseEntity.ok(buildSuccessResponse(null, "留言狀態更新成功"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}
    
    /**
     * 刪除換票貼文
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> removeSwapPost(@PathVariable Integer postId, HttpSession session) {
    	try {
            Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            ticketExchangeService.removeSwapPost(postId, memberId);
            return ResponseEntity.ok(buildSuccessResponse(null, "換票貼文刪除成功"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤"));
        }
	}
    
    /**
     * 從Session獲取會員ID
     */
    private Integer getMemberIdFromSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        Object memberObj = session.getAttribute("member");
        if (memberObj instanceof user.member.vo.Member) {
            user.member.vo.Member member = (user.member.vo.Member) memberObj;
            return member.getMemberId();
        }
        
        return null;
    }

    private Map<String, Object> buildSuccessResponse(Object data, Object message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        if (message instanceof Integer) {
            response.put("total", message);
        } else {
            response.put("message", message);
        }
        return response;
    }

    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return errorResponse;
    }

    @SuppressWarnings("unchecked")
    private void addPhotoUrls(List<Map<String, Object>> dataList) {
        for (Map<String, Object> data : dataList) {
            addPhotoUrl(data);
        }
    }

    @SuppressWarnings("unchecked")
    private void addPhotoUrl(Map<String, Object> data) {
        Map<String, Object> member = (Map<String, Object>) data.get("member");
        if (member != null && member.get("memberId") != null) {
            Integer memberId = (Integer) member.get("memberId");
            member.put("photoUrl", "/api/member-photos/" + memberId);
        }
    }
}