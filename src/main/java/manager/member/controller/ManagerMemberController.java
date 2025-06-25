package manager.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import manager.member.service.ManagerMemberService;
import user.member.vo.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 會員管理控制器
 * 創建者: archchang
 * 創建日期: 2025-06-25
 */
@RestController
@RequestMapping("/api/manager/member")
public class ManagerMemberController {

	@Autowired
	private ManagerMemberService memberService;

	/**
	 * 查詢所有會員
	 * 
	 * @return 會員列表
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> listMembers() {
		try {
			List<Member> members = memberService.listMembers();
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", members);
			response.put("message", "查詢成功");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "查詢失敗：" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * 分頁查詢會員
	 * 
	 * @param userName  使用者名稱
	 * @param startDate 開始日期
	 * @param endDate   結束日期
	 * @param roleLevel 會員等級
	 * @param isActive  啟用狀態
	 * @param page      頁碼
	 * @param size      每頁筆數
	 * @return 分頁結果
	 */
	@GetMapping("/page")
	public ResponseEntity<Map<String, Object>> getMemberPage(@RequestParam(required = false) String userName,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) Integer roleLevel, @RequestParam(required = false) Integer isActive,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			Map<String, Object> result = memberService.getMemberPage(userName, startDate, endDate, roleLevel, isActive,
					page, size);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", result);
			response.put("message", "查詢成功");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "查詢失敗：" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * 根據ID查詢會員
	 * 
	 * @param memberId 會員ID
	 * @return 會員資料
	 */
	@GetMapping("/{memberId}")
	public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable Integer memberId) {
		try {
			Member member = memberService.getMemberById(memberId);
			Map<String, Object> response = new HashMap<>();
			if (member != null) {
				response.put("success", true);
				response.put("data", member);
				response.put("message", "查詢成功");
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "找不到指定的會員");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		} catch (Exception e) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "查詢失敗：" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}