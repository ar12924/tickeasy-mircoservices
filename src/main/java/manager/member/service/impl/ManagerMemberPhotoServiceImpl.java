package manager.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import manager.member.dao.ManagerMemberPhotoDao;
import manager.member.service.ManagerMemberPhotoService;

import java.io.IOException;
import java.io.InputStream;

/**
 * 會員照片服務實作類
 * 創建者: archchang
 * 創建日期: 2025-06-27
 */
@Service
@Transactional
public class ManagerMemberPhotoServiceImpl implements ManagerMemberPhotoService {

    @Autowired
    private ManagerMemberPhotoDao memberPhotoDao;

    @Override
    public void uploadMemberPhoto(Integer memberId, MultipartFile photoFile) {
        if (memberId == null) {
            throw new IllegalArgumentException("會員ID不能為空");
        }

        if (photoFile == null || photoFile.isEmpty()) {
            throw new IllegalArgumentException("照片檔案不能為空");
        }

        try {
            try (InputStream inputStream = photoFile.getInputStream()) {
                byte[] photoData = inputStream.readAllBytes();
                
                int updatedRows = memberPhotoDao.uploadPhoto(memberId, photoData);
                if (updatedRows == 0) {
                    throw new IllegalArgumentException("找不到指定的會員");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("照片檔案讀取失敗", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getMemberPhoto(Integer memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("會員ID不能為空");
        }

        return memberPhotoDao.getPhoto(memberId);
    }
}