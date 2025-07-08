package user.ticket.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
	
	@PersistenceContext
	private EntityManager entityManager;
	
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
			if (requestData == null) {
	            return ResponseEntity.badRequest().body(buildErrorResponse("請求資料不能為空"));
	        }
			
			Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }
            
            Object ticketIdObj = requestData.get("ticketId");
            Object descriptionObj = requestData.get("description");
            Object eventIdObj = requestData.get("eventId");
            
            if (ticketIdObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請選擇票券"));
            }
            if (descriptionObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請輸入貼文描述"));
            }
            if (eventIdObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("缺少活動ID"));
            }
            
            Integer ticketId = safeConvertToInteger(ticketIdObj);
            String description = safeConvertToString(descriptionObj);
            Integer eventId = safeConvertToInteger(eventIdObj);
            
            if (ticketId == null || ticketId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的票券ID"));
            }
            if (eventId == null || eventId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的活動ID"));
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(buildErrorResponse("貼文描述不能為空"));
            }

            Map<String, Object> data = ticketExchangeService.createSwapPost(memberId, ticketId, description, eventId);
            addPhotoUrl(data);

            return ResponseEntity.ok(buildSuccessResponse(data, "換票貼文創建成功"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
        	String message = e.getMessage();
        	if (message.contains("已對此活動發布")) {
                return ResponseEntity.badRequest().body(buildErrorResponse("已對此活動發布過換票貼文"));
            } else if (message.contains("已用於其他轉票")) {
                return ResponseEntity.badRequest().body(buildErrorResponse("票券已用於其他轉票"));
            } else if (message.contains("票券不屬於")) {
                return ResponseEntity.badRequest().body(buildErrorResponse("票券不屬於您"));
            } else {
                return ResponseEntity.badRequest().body(buildErrorResponse(message));
            }
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
    		if (requestData == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請求資料不能為空"));
            }
    		
    		Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            
            Object postIdObj = requestData.get("postId");
            Object ticketIdObj = requestData.get("ticketId");
            Object descriptionObj = requestData.get("description");
            
            if (postIdObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("缺少貼文ID"));
            }
            if (ticketIdObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請選擇票券"));
            }
            if (descriptionObj == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請輸入留言內容"));
            }
            
            
            Integer postId = safeConvertToInteger(postIdObj);
            Integer ticketId = safeConvertToInteger(ticketIdObj);
            String description = safeConvertToString(descriptionObj);

            if (postId == null || postId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的貼文ID"));
            }
            if (ticketId == null || ticketId <= 0) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的票券ID"));
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(buildErrorResponse("留言內容不能為空"));
            }
            
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
//            addPhotoUrls(comments);
            
            if (comments == null) {
                comments = new ArrayList<>();
            }
            for (Map<String, Object> comment : comments) {
                if (comment != null && comment.get("member") != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> member = (Map<String, Object>) comment.get("member");
                    if (member.get("memberId") != null) {
                        member.put("photoUrl", "/api/member-photos/" + member.get("memberId"));
                    }
                }
            }
            return ResponseEntity.ok(buildSuccessResponse(comments, comments.size()));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
        	e.printStackTrace();
            
            return ResponseEntity.ok(buildSuccessResponse(new ArrayList<>(), 0));
        }
	}

    /**
     * 更新換票留言狀態
     */
    @PutMapping("/comments/{commentId}/status")
    public ResponseEntity<Map<String, Object>> updateSwapCommentStatus(@PathVariable Integer commentId, @RequestBody Map<String, Object> requestData, HttpSession session) {
    	try {
    		if (requestData == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("請求資料不能為空"));
            }
    		
    		Integer memberId = getMemberIdFromSession(session);
            if (memberId == null) {
                return ResponseEntity.status(401).body(buildErrorResponse("未登入或登入已過期"));
            }

            Integer status = safeConvertToInteger(requestData.get("status"));
            if (status == null) {
                return ResponseEntity.badRequest().body(buildErrorResponse("缺少狀態參數"));
            }
            if (status < 0 || status > 3) {
                return ResponseEntity.badRequest().body(buildErrorResponse("無效的狀態值"));
            }

            // 調用Service方法
            ticketExchangeService.updateSwapCommentStatus(commentId, status, memberId);
            
            // 🔧 新增：清除EntityManager快取確保資料同步
            entityManager.clear();
            
            // 🔧 新增：改善回應訊息
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", getStatusUpdateMessage(status));
            response.put("updatedStatus", status); 
            response.put("commentId", commentId);  
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(buildErrorResponse("系統錯誤：" + e.getMessage()));
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
        if (memberObj instanceof Member) {
            Member member = (Member) memberObj;
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
    
    
    private Integer safeConvertToInteger(Object value) {
    	if (value == null) {
            return null;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            try {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                
                // 處理 "true"/"false" 字串
                if ("true".equalsIgnoreCase(str)) {
                    return 1;
                }
                if ("false".equalsIgnoreCase(str)) {
                    return 0;
                }
                
                if (str.contains(".")) {
                    return (int) Double.parseDouble(str);
                }
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        // 處理 Boolean 類型
        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        }
        
        return null;
    }

    private String safeConvertToString(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof String) {
            return (String) value;
        }
        
        return String.valueOf(value);
    }
    
    private String getStatusUpdateMessage(Integer status) {
        switch (status) {
            case 1: return "已接受換票請求";
            case 2: return "票券交換完成！";
            case 3: return "已取消換票請求";
            default: return "狀態更新成功";
        }
    }
    
}