package manager.member.dao;

import user.member.vo.Member;
import java.util.List;
import java.util.Map;

/**
 * 會員資料存取介面
 * 創建者: archchang
 * 創建日期: 2025-06-25
 */
public interface ManagerMemberDao {

	/**
	 * 查詢所有會員
	 * 
	 * @return 會員列表
	 */
	List<Member> findAll();

	/**
	 * 根據會員ID查詢會員
	 * 
	 * @param memberId 會員ID
	 * @return 會員物件
	 */
	Member findById(Integer memberId);

	/**
	 * 根據使用者名稱查詢會員
	 * 
	 * @param userName 使用者名稱
	 * @return 會員列表
	 */
	List<Member> findByUserName(String userName);

	/**
	 * 根據會員等級查詢會員
	 * 
	 * @param roleLevel 會員等級
	 * @return 會員列表
	 */
	List<Member> findByRoleLevel(Integer roleLevel);

	/**
	 * 根據啟用狀態查詢會員
	 * 
	 * @param isActive 啟用狀態
	 * @return 會員列表
	 */
	List<Member> findByIsActive(Integer isActive);

	/**
	 * 根據日期範圍查詢會員
	 * 
	 * @param startDate 開始日期
	 * @param endDate   結束日期
	 * @return 會員列表
	 */
	List<Member> findByDateRange(String startDate, String endDate);

	/**
	 * 分頁查詢所有會員
	 * 
	 * @param offset 偏移量
	 * @param limit  限制筆數
	 * @return 會員列表
	 */
	List<Member> findAllWithPaging(int offset, int limit);

	/**
	 * 計算會員總數
	 * 
	 * @return 總數
	 */
	long count();
	
	List<Member> findByDynamicQuery(String hql, Map<String, Object> parameters, int offset, int limit);
    
	long countByDynamicQuery(String hql, Map<String, Object> parameters);
}