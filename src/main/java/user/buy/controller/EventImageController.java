package user.buy.controller;

import common.util.CommonUtil;
import user.buy.service.EventInfoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 活動圖片控制器，專門處理活動圖片相關請求
 * 創建者: archchang
 * 創建日期: 2025-06-03
 */
@RestController
@RequestMapping("/api/events/image")
public class EventImageController {
	@Autowired
    private EventInfoService eventService;
	
	@GetMapping("/{eventId}")
    public void getEventImage(@PathVariable Integer eventId, HttpServletResponse response) throws IOException{
        
		try (InputStream imageStream = eventService.getEventImageStream(eventId)) {
            if (imageStream != null) {
                // 設定響應標頭
                response.setContentType("image/jpeg");
                response.setHeader("Cache-Control", "public, max-age=3600");
                
                // 串流傳輸圖片
                try (OutputStream outputStream = response.getOutputStream()) {
                    byte[] buffer = new byte[8192]; // 8KB 緩衝區
                    int bytesRead;
                    while ((bytesRead = imageStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "圖片不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "獲取圖片時發生錯誤");
        }
		/*
		byte[] imageData = eventService.getEventImage(eventId);
        
        if (imageData != null && imageData.length > 0) {
        	response.setContentType("image/jpeg");
            response.setContentLength(imageData.length);
            response.setHeader("Cache-Control", "public, max-age=3600");
            
            try (OutputStream out = response.getOutputStream()) {
                out.write(imageData);
            }
        } else {
        	response.sendError(HttpServletResponse.SC_NOT_FOUND, "圖片不存在");
        }
        */
    }
}