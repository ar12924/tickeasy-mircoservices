package user.buy.controller;

import com.google.gson.Gson;

import common.util.CommonUtil;
import user.buy.service.EventInfoService;
import user.buy.service.impl.EventInfoServiceImpl;
import user.buy.vo.TicketTypeVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活動票券類型控制器
 * 創建日期: 2025-05-12
 */
@RestController
@RequestMapping("/api/event/tickets")
public class EventTicketsController extends HttpServlet {
	@Autowired
    private EventInfoService eventService;
	
	@GetMapping("/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventTicketTypes(@PathVariable Integer eventId) {
        List<TicketTypeVO> ticketTypes = eventService.getEventTicketTypes(eventId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            response.put("status", 200);
            response.put("data", ticketTypes);
        } else {
            response.put("status", 404);
            response.put("errorCode", "B0010");
            response.put("errorMessage", "找不到票券類型");
            response.put("userMessage", "該活動尚未設置票券類型");
        }
        
        return ResponseEntity.ok(response);
    }

}