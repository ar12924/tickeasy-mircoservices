package user.ticket.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.util.CommonUtil;
import user.ticket.service.TicketExchangeService;
import user.member.vo.Member;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 票券交換控制器 創建者: archchang 創建日期: 2025-05-26
 */
@WebServlet("/api/ticket-exchange/*")
public class TicketExchangeController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TicketExchangeService ticketExchangeService;
	private Gson gson;

	@Override
	public void init() throws ServletException {
		super.init();
		ticketExchangeService = CommonUtil.getBean(getServletContext(), TicketExchangeService.class);
		gson = new Gson();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();
		PrintWriter out = resp.getWriter();

		try {
			if (pathInfo == null) {
				buildErrorResponse(out, 400, "A0001", "無效的請求路徑", "請提供有效的請求路徑");
				return;
			}

			String[] pathParts = pathInfo.substring(1).split("/");

			if (pathParts.length >= 3 && "posts".equals(pathParts[0]) && "event".equals(pathParts[1])) {
				// GET /api/ticket-exchange/posts/event/{eventId}
				handleGetSwapPostsByEventId(pathParts[2], out);
			} else if (pathParts.length >= 3 && "posts".equals(pathParts[0]) && "member".equals(pathParts[1])) {
				// GET /api/ticket-exchange/posts/member/{memberId}
				handleGetMemberSwapPosts(pathParts[2], out);
			} else if (pathParts.length >= 3 && "comments".equals(pathParts[0]) && "member".equals(pathParts[1])) {
				// GET /api/ticket-exchange/comments/member/{memberId}
				handleGetMemberSwapComments(pathParts[2], out);
			} else if (pathParts.length >= 3 && "posts".equals(pathParts[0]) && "comments".equals(pathParts[2])) {
				// GET /api/ticket-exchange/posts/{postId}/comments
				handleGetSwapCommentsByPostId(pathParts[1], out);
			} else {
				buildErrorResponse(out, 404, "A0008", "不支援的請求路徑", "請檢查請求路徑是否正確");
			}

		} catch (Exception e) {
			e.printStackTrace();
			buildErrorResponse(out, 500, "B0001", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();
		PrintWriter out = resp.getWriter();

		try {
			if (pathInfo == null) {
				buildErrorResponse(out, 400, "A0002", "無效的請求路徑", "請提供有效的請求路徑");
				return;
			}

			String[] pathParts = pathInfo.substring(1).split("/");

			if (pathParts.length == 1 && "posts".equals(pathParts[0])) {
				// POST /api/ticket-exchange/posts
				handleCreateSwapPost(req, out);
			} else if (pathParts.length == 1 && "comments".equals(pathParts[0])) {
				// POST /api/ticket-exchange/comments
				handleCreateSwapComment(req, out);
			} else {
				buildErrorResponse(out, 404, "A0009", "不支援的請求路徑", "請檢查請求路徑是否正確");
			}

		} catch (Exception e) {
			e.printStackTrace();
			buildErrorResponse(out, 500, "B0002", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();
		PrintWriter out = resp.getWriter();

		try {
			if (pathInfo == null) {
				buildErrorResponse(out, 400, "A0006", "無效的請求路徑", "請提供有效的請求路徑");
				return;
			}

			String[] pathParts = pathInfo.substring(1).split("/");

			if (pathParts.length == 2 && "posts".equals(pathParts[0])) {
				// DELETE /api/ticket-exchange/posts/{postId}
				handleRemoveSwapPost(pathParts[1], req, out);
			} else {
				buildErrorResponse(out, 404, "A0010", "不支援的請求路徑", "請檢查請求路徑是否正確");
			}

		} catch (Exception e) {
			e.printStackTrace();
			buildErrorResponse(out, 500, "B0006", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		String pathInfo = req.getPathInfo();
		PrintWriter out = resp.getWriter();

		try {
			if (pathInfo == null) {
				buildErrorResponse(out, 400, "A0009", "無效的請求路徑", "請提供有效的請求路徑");
				return;
			}

			String[] pathParts = pathInfo.substring(1).split("/");

			if (pathParts.length == 3 && "comments".equals(pathParts[0]) && "status".equals(pathParts[2])) {
				// PUT /api/ticket-exchange/comments/{commentId}/status
				handleUpdateSwapCommentStatus(pathParts[1], req, out);
			} else {
				buildErrorResponse(out, 404, "A0011", "不支援的請求路徑", "請檢查請求路徑是否正確");
			}

		} catch (Exception e) {
			e.printStackTrace();
			buildErrorResponse(out, 500, "B0009", "系統內部錯誤: " + e.getMessage(), "系統暫時無法處理您的請求，請稍後再試");
		}
	}

	private void handleGetSwapPostsByEventId(String eventIdStr, PrintWriter out) {
		try {
			Integer eventId = Integer.parseInt(eventIdStr);
			List<Map<String, Object>> posts = ticketExchangeService.listSwapPostsByEventId(eventId);
			posts.forEach(this::addPhotoUrlToData);

			Map<String, Object> response = buildSuccessResponse(posts, posts.size());
			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0001", "無效的活動ID", "請提供有效的活動ID");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0001", e.getMessage(), "請提供有效的活動ID");
		}
	}

	private void handleCreateSwapPost(HttpServletRequest req, PrintWriter out) throws IOException {
		try {
			 // 從session獲取會員資訊
	        Map<String, Object> memberInfo = getMemberFromSession(req);
	        if (memberInfo == null) {
	            buildErrorResponse(out, 401, "A0001", "未登入或登入已過期", "請重新登入");
	            return;
	        }
	        
	        Integer memberId = (Integer) memberInfo.get("memberId");  // ← 從session獲取
	        
	        String requestBody = getRequestBody(req);
	        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
	        
	        
	        Integer ticketId = jsonObject.get("ticketId").getAsInt();
	        String description = jsonObject.get("description").getAsString();
	        Integer eventId = jsonObject.get("eventId").getAsInt();

			Map<String, Object> data = ticketExchangeService.createSwapPost(memberId, ticketId, description, eventId);
			addPhotoUrlToData(data);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", data);
			response.put("message", "換票貼文創建成功");

			out.println(gson.toJson(response));

		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0002", e.getMessage(), "請檢查輸入的資料是否正確");
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
			String userMessage = getUserMessageByError(e.getMessage());
			buildErrorResponse(out, 400, errorCode, e.getMessage(), userMessage);
		}
	}

	private void handleCreateSwapComment(HttpServletRequest req, PrintWriter out) throws IOException {
		try {
			// 從session獲取會員資訊
	        Map<String, Object> memberInfo = getMemberFromSession(req);
	        if (memberInfo == null) {
	            buildErrorResponse(out, 401, "A0001", "未登入或登入已過期", "請重新登入");
	            return;
	        }
	        
	        Integer memberId = (Integer) memberInfo.get("memberId");  
	        
	        String requestBody = getRequestBody(req);
	        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
	        
	        Integer postId = jsonObject.get("postId").getAsInt();
	        
	        Integer ticketId = jsonObject.get("ticketId").getAsInt();
	        String description = jsonObject.get("description").getAsString();

			Map<String, Object> data = ticketExchangeService.createSwapComment(postId, memberId, ticketId, description);
			addPhotoUrlToData(data);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", data);
			response.put("message", "換票留言創建成功");

			out.println(gson.toJson(response));

		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0003", e.getMessage(), "請檢查輸入的資料是否正確");
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
			String userMessage = getUserMessageByError(e.getMessage());
			buildErrorResponse(out, 400, errorCode, e.getMessage(), userMessage);
		}
	}

	private void handleGetMemberSwapPosts(String memberIdStr, PrintWriter out) {
		try {
			Integer memberId = Integer.parseInt(memberIdStr);
			List<Map<String, Object>> posts = ticketExchangeService.listMemberSwapPosts(memberId);
			posts.forEach(this::addPhotoUrlToData);

			Map<String, Object> response = buildSuccessResponse(posts, posts.size());
			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0004", "無效的會員ID", "請提供有效的會員ID");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0004", e.getMessage(), "請提供有效的會員ID");
		}
	}

	private void handleGetMemberSwapComments(String memberIdStr, PrintWriter out) {
		try {
			Integer memberId = Integer.parseInt(memberIdStr);
			List<Map<String, Object>> comments = ticketExchangeService.listMemberSwapComments(memberId);
			comments.forEach(this::addPhotoUrlToData);

			Map<String, Object> response = buildSuccessResponse(comments, comments.size());
			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0005", "無效的會員ID", "請提供有效的會員ID");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0005", e.getMessage(), "請提供有效的會員ID");
		}
	}

	private void handleGetSwapCommentsByPostId(String postIdStr, PrintWriter out) {
		try {
			Integer postId = Integer.parseInt(postIdStr);
			List<Map<String, Object>> comments = ticketExchangeService.listSwapCommentsByPostId(postId);
			comments.forEach(this::addPhotoUrlToData);

			Map<String, Object> response = buildSuccessResponse(comments, comments.size());
			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0008", "無效的貼文ID", "請提供有效的貼文ID");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0008", e.getMessage(), "請提供有效的貼文ID");
		}
	}

	private void handleRemoveSwapPost(String postIdStr, HttpServletRequest req, PrintWriter out) throws IOException {
		try {
			// 從session獲取會員資訊
	        Map<String, Object> memberInfo = getMemberFromSession(req);
	        if (memberInfo == null) {
	            buildErrorResponse(out, 401, "A0001", "未登入或登入已過期", "請重新登入");
	            return;
	        }
	        
	        Integer memberId = (Integer) memberInfo.get("memberId");  
	        Integer postId = Integer.parseInt(postIdStr);

			ticketExchangeService.removeSwapPost(postId, memberId);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "換票貼文刪除成功");

			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0006", "無效的貼文ID", "請提供有效的參數");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0006", e.getMessage(), "請提供有效的參數");
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
			String userMessage = getUserMessageByError(e.getMessage());
			buildErrorResponse(out, 400, errorCode, e.getMessage(), userMessage);
		}
	}

	private void handleUpdateSwapCommentStatus(String commentIdStr, HttpServletRequest req, PrintWriter out)
			throws IOException {
		try {
			// 從session獲取會員資訊
	        Map<String, Object> memberInfo = getMemberFromSession(req);
	        if (memberInfo == null) {
	            buildErrorResponse(out, 401, "A0001", "未登入或登入已過期", "請重新登入");
	            return;
	        }
	        
	        Integer memberId = (Integer) memberInfo.get("memberId");
	        Integer commentId = Integer.parseInt(commentIdStr);
	        
	        String requestBody = getRequestBody(req);
	        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
	        Integer status = jsonObject.get("status").getAsInt();

			ticketExchangeService.updateSwapCommentStatus(commentId, status, memberId);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("message", "留言狀態更新成功");

			out.println(gson.toJson(response));

		} catch (NumberFormatException e) {
			buildErrorResponse(out, 400, "A0009", "無效的留言ID", "請提供有效的參數");
		} catch (IllegalArgumentException e) {
			buildErrorResponse(out, 400, "A0009", e.getMessage(), "請提供有效的參數");
		} catch (RuntimeException e) {
			String errorCode = getErrorCodeFromMessage(e.getMessage());
			String userMessage = getUserMessageByError(e.getMessage());
			buildErrorResponse(out, 400, errorCode, e.getMessage(), userMessage);
		}
	}

	private String getRequestBody(HttpServletRequest req) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = req.getReader().readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	private Map<String, Object> buildSuccessResponse(Object data, Integer total) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("data", data);
		if (total != null) {
			response.put("total", total);
		}
		return response;
	}

	private void buildErrorResponse(PrintWriter out, int httpStatus, String errorCode, String errorMessage,
			String userMessage) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("httpStatus", httpStatus);
		errorResponse.put("errorCode", errorCode);
		errorResponse.put("errorMessage", errorMessage);
		errorResponse.put("userMessage", userMessage);
		errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());

		out.println(gson.toJson(errorResponse));
	}

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
	private Map<String, Object> getMemberFromSession(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null) {
			return null;
		}

		Member member = (Member) session.getAttribute("member");
		if (member == null) {
			return null;
		}

		// 建立會員資訊 Map
	    Map<String, Object> memberInfo = new HashMap<>();
	    memberInfo.put("memberId", member.getMemberId());
	    memberInfo.put("nickname", member.getNickName());
	    memberInfo.put("email", member.getEmail());
	    memberInfo.put("roleLevel", member.getRoleLevel());
	    
	    return memberInfo;
	}
}