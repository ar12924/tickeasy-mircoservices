package manager.member.dao;

/**
 * 會員照片資料存取介面 
 * 創建者: archchang 
 * 創建日期: 2025-06-27
 */
public interface ManagerMemberPhotoDao {

	/**
	 * 上傳會員照片
	 * @param memberId  會員ID
	 * @param photoData 照片資料
	 * @return 更新的記錄數
	 */
	int uploadPhoto(Integer memberId, byte[] photoData);

	/**
	 * 獲取會員照片
	 * 
	 * @param memberId 會員ID
	 * @return 照片資料
	 */
	byte[] getPhoto(Integer memberId);
}
