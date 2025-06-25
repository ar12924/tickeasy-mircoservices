package manager.member.service;

import user.member.vo.Member;
import java.util.List;
import java.util.Map;

/**
 * 會員業務邏輯介面
 * 創建者: archchang
 * 創建日期: 2025-06-25
 */
public interface ManagerMemberService {

	/**
	 * 查詢所有會員
	 * 
	 * @return 會員列表
	 */
	List<Member> listMembers();

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
	Map<String, Object> getMemberPage(String userName, String startDate, String endDate, Integer roleLevel,
			Integer isActive, int page, int size);

	/**
	 * 根據會員ID查詢會員
	 * 
	 * @param memberId 會員ID
	 * @return 會員物件
	 */
	Member getMemberById(Integer memberId);
}