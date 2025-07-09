package manager.member.controller;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import manager.member.service.ManagerMemberPhotoService;
/**
 * 會員照片管理控制器
 * 創建者: archchang
 * 創建日期: 2025-06-27
 */
@RestController
@RequestMapping("/api/manager/member/photo")
public class ManagerMemberPhotoController {
	@Autowired
    private ManagerMemberPhotoService memberPhotoService;

    /**
     * 上傳會員照片
     * 
     * @param memberId  會員ID
     * @param photoFile 照片檔案
     * @return 上傳結果
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<Map<String, Object>> uploadMemberPhoto(
            @PathVariable Integer memberId,
            @RequestParam("photo") MultipartFile photoFile) {
        try {
            memberPhotoService.uploadMemberPhoto(memberId, photoFile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "照片上傳成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "照片上傳失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 獲取會員照片
     * 
     * @param memberId 會員ID
     * @return 照片檔案流
     */
    @GetMapping("/{memberId}")
    public void getMemberPhoto(@PathVariable Integer memberId, HttpServletResponse response) {
        try {
            byte[] photoData = memberPhotoService.getMemberPhoto(memberId);

            if (photoData == null || photoData.length == 0) {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

         // 設定回應標頭
            response.setContentType("image/jpeg");
            response.setContentLength(photoData.length);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // 直接寫入回應流
            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(photoData);
                outputStream.flush();
            }
        } catch (IllegalArgumentException e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
