package manager.member.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 會員照片服務介面
 * 創建者: archchang
 * 創建日期: 2025-06-27
 */
public interface ManagerMemberPhotoService {
	/**
     * 上傳會員照片
     * 
     * @param memberId  會員ID
     * @param photoFile 照片檔案
     */
    void uploadMemberPhoto(Integer memberId, MultipartFile photoFile);

    /**
     * 獲取會員照片
     * 
     * @param memberId 會員ID
     * @return 照片位元組陣列
     */
    byte[] getMemberPhoto(Integer memberId);
}
