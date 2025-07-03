package user.ticket.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.ticket.dao.TicketDao;
import user.ticket.dto.TicketViewDto;
import user.ticket.service.TicketService;
import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;


@Service
public class TicketServiceImpl implements TicketService{

	@Autowired
	private TicketDao ticketDao;

	/*
	 * public TicketServiceImpl() throws NamingException { ticketDao = new
	 * TicketDaoImpl(); }
	 */
	
	@Transactional
	@Override
	public List<TicketViewDto> ticketList(int memberId) {
		
		List<Ticket> tickets = ticketDao.selectAllByMemberId(memberId);
		List<TicketViewDto> result= new ArrayList<>();


		for(Ticket ticket:tickets) {
			TicketViewDto dto = new TicketViewDto();
			
			dto.setTicketId(ticket.getTicketId());
			dto.setOrderId(ticket.getOrderId());
			dto.setEmail(ticket.getEmail());
			dto.setPhone(ticket.getPhone());
			dto.setPrice(ticket.getPrice());
			dto.setStatus(ticket.getStatus());
			dto.setIdCard(ticket.getIdCard());
			dto.setCurrentHolderMemberId(ticket.getCurrentHolderMemberId());
			dto.setMemberId(ticket.getBuyerOrderTicketVer().getMemberId());
			dto.setIsUsed(ticket.getIsUsed());
			dto.setParticipantName(ticket.getParticipantName());
			dto.setEventName(ticket.getEventName());
			dto.setCategoryName(ticket.getEventTicketTypeTicketVer().getCategoryName());
			dto.setQueueId(ticket.getQueueId());
			dto.setEventFromDate(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate());
			dto.setPlace(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getPlace());
			dto.setCreateTime(ticket.getCreateTime());
			dto.setUpdateTime(ticket.getUpdateTime());
			
			//狀態轉換邏輯
			if(ticket.getStatus()==1) {
				dto.setStatusText("已付款");
			}else {
				dto.setStatusText("未付款");
			}
			if(ticket.getIsUsed()==1) {
				dto.setIsUsedText("已使用");
			}else {
				dto.setIsUsedText("未使用");
			}
			result.add(dto);
			
		}
		
		
		
		System.out.println(result);
		
		return result;
		
	}

}
