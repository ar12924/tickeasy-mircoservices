package user.ticket.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import user.ticket.dao.SwapPostDao;

/**
 * 會員照片控制器
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
@RestController
@RequestMapping("/api/member-photos")
public class MemberPhotoController {

	@Autowired
    private SwapPostDao swapPostDao;

	/**
     * 獲取會員照片
     */
    @GetMapping("/{memberId}")
    public void getMemberPhoto(@PathVariable Integer memberId, HttpServletResponse response) throws IOException {
        // 參數驗證
        if (memberId == null || memberId <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "無效的會員ID");
            return;
        }

        try (InputStream photoStream = swapPostDao.getMemberPhotoStream(memberId)) {
            
            if (photoStream != null) {
                // 設定回應標頭
                response.setContentType("image/jpeg");
                response.setHeader("Cache-Control", "max-age=3600");
                response.setHeader("Content-Disposition", "inline");
                
                // 使用 try-with-resources 自動關閉 BufferedInputStream
                try (BufferedInputStream bufferedStream = new BufferedInputStream(photoStream, 8192)) {
                    // 直接串流傳輸，減少記憶體使用
                    bufferedStream.transferTo(response.getOutputStream());
                    response.getOutputStream().flush();
                }
                
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "找不到會員照片");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "獲取照片時發生錯誤");
        }
    }
}