package manager.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import manager.member.service.ManagerMemberPhotoService;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<InputStreamResource> getMemberPhoto(@PathVariable Integer memberId) {
        try {
            byte[] photoData = memberPhotoService.getMemberPhoto(memberId);

            if (photoData == null || photoData.length == 0) {
                return ResponseEntity.notFound().build();
            }

            // 使用 Stream 回傳照片
            ByteArrayInputStream inputStream = new ByteArrayInputStream(photoData);
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(photoData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
