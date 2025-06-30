package manager.member.dao;

import java.util.List;

import user.member.vo.Member;

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
     * 分頁查詢會員（支援複合條件）
     * DAO層負責 Native SQL 查詢邏輯構建
     * 
     * @param userName  使用者名稱（模糊搜尋）
     * @param startDate 開始日期
     * @param endDate   結束日期
     * @param roleLevel 會員等級
     * @param isActive  啟用狀態
     * @param offset    偏移量
     * @param limit     限制筆數
     * @return 會員列表
     */
    List<Member> findMembersWithConditions(String userName, String startDate, String endDate, 
                                          Integer roleLevel, Integer isActive, int offset, int limit);

    /**
     * 計算符合條件的會員總數
     * DAO層負責 Native SQL 計數查詢邏輯構建
     * 
     * @param userName  使用者名稱（模糊搜尋）
     * @param startDate 開始日期
     * @param endDate   結束日期
     * @param roleLevel 會員等級
     * @param isActive  啟用狀態
     * @return 總數
     */
    long countMembersWithConditions(String userName, String startDate, String endDate, 
                                   Integer roleLevel, Integer isActive);

	/**
	 * 計算會員總數
	 * 
	 * @return 總數
	 */
	long count();
	
	/**
     * 新增會員
     * 
     * @param member 會員物件
     * @return 新增的會員物件
     */
    Member save(Member member);

    /**
     * 更新會員
     * 
     * @param member 會員物件
     * @return 更新後的會員物件
     */
    Member update(Member member);

    /**
     * 刪除會員
     * 
     * @param memberId 會員ID
     */
    void deleteById(Integer memberId);

    /**
     * 根據使用者名稱查詢會員
     * 
     * @param userName 使用者名稱
     * @return 會員物件
     */
    Member findByUserName(String userName);

    /**
     * 根據電子郵件查詢會員
     * 
     * @param email 電子郵件
     * @return 會員物件
     */
    Member findByEmail(String email);
    
    /**
     * 根據手機號碼查詢會員
     * 
     * @param phone 手機號碼
     * @return 會員物件
     */
    Member findByPhone(String phone);
}