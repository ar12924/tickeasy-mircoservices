package user.ticket.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import user.ticket.dto.TicketViewDto;
import user.ticket.service.TicketService;
import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;

@Controller
@RequestMapping("ticket")
public class UseTicketController {
	@Autowired
	private TicketService ticketService;
	
	@GetMapping("use")
	public void useTicket(@RequestParam("Id") String ticketId,@RequestParam("code") String customCode,HttpServletResponse response) throws IOException {

		
		 // 設置回應編碼為 UTF-8
        response.setContentType("text/plain;charset=UTF-8"); // 設置內容類型和編碼
        response.setCharacterEncoding("UTF-8"); // 設置字符編碼為 UTF-8

        // 寫入回應內容
        PrintWriter out = response.getWriter();
        
	    // 驗證該票券存在、身分證符合、code 正確
	    TicketViewDto ticket = ticketService.findByTicketId(Integer.parseInt(ticketId));

	    if (ticket == null) {
	    	out.print("❌ 驗票失敗：查無此票券");
            return ;
        }

        // 2. 驗證 code（通常是加密/雜湊）
        if (!customCode.equals(ticket.getQrCodeHashCode())) {
        	out.print("❌ 驗票失敗：驗証不成功;");
            return ;
        }

        // 3. 檢查是否已使用
        if (ticket.getIsUsed() == 1) {
        	out.print("⚠️ 此票券已使用過");
            return ;
        }

        // 4. 更新為已使用
     /*   ticket.setStatus(1);
        ticket.setUsedTime(now());*/
        ticketService.updateTicketStatus(Integer.parseInt(ticketId)); // 這要連到你定義的 update 方法

        out.print("✅ 驗票成功！票券已設定為已使用。");
    }
}
	    
	    
	  


