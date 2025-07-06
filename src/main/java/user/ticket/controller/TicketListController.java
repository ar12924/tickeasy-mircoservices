package user.ticket.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import user.member.vo.Member;
import user.ticket.dto.TicketViewDto;
import user.ticket.service.TicketService;




@Controller
@RequestMapping("ticket")

public class TicketListController{
	
	@Autowired
	private TicketService ticketService;
	
	@GetMapping("check-login")
    @ResponseBody
    public boolean checkLoginStatus(@SessionAttribute(required = false) Member member) {
        return member != null && member.getMemberId() != null;
    }
	
	@PostMapping("ticket-list")
	@ResponseBody
	public List<TicketViewDto> ticketList(/* @RequestBody Member member */ @SessionAttribute  (required = false) Member member) {
		
    	if (member == null || member.getMemberId() == null) {
            System.out.println("未登入");
            return new ArrayList<>();
            }
    	Integer memId=member.getMemberId();

		
		List<TicketViewDto> ticketsView =  ticketService.ticketList(memId);

		return ticketsView;
		
	}
}














/*




@WebServlet("/ticket-list")
public class TicketListController extends HttpServlet{

	private static final long serialVersionUID = 1L;
		private TicketService ticketService;
		public void init() throws ServletException {
				try {
					ticketService = new TicketServiceImpl();
				} catch (NamingException e) {
				
					e.printStackTrace();
				}
				
				 // ticketService =CommonUtil.getBean(getServletContext(),TicketService.class);//
				 
			
		}
		
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Gson gson =new Gson();
			JsonObject info= gson.fromJson(req.getReader(),JsonObject.class);
	    	String memId=info.get("memberId").getAsString();
		


			
			List<TicketViewDto> ticketsView = ticketService.ticketList(Integer.parseInt(memId));
			
			String json = gson.toJson(ticketsView);
			
			resp.setContentType("application/json");
			resp.getWriter().write(json);

		}

	}
*/

