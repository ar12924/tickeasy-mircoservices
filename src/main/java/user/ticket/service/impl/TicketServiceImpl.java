package user.ticket.service.impl;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;


import user.notify.dao.NotificationDao;
import user.notify.dao.impl.NotificationDaoImpl;
import user.notify.vo.Notification;
import user.ticket.dao.TicketDao;
import user.ticket.dao.impl.TicketDaoImpl;
import user.ticket.dto.TicketViewDto;
import user.ticket.service.TicketService;
import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;

public class TicketServiceImpl implements TicketService{

	private TicketDao ticketDao;

	public TicketServiceImpl() throws NamingException {
		ticketDao = new TicketDaoImpl();
	}
	

	@Override
	public List<TicketViewDto> ticketList(int memberId) {
		
		List<TicketView> tickets = ticketDao.selectAllByMemberId(memberId);
		List<TicketViewDto> result= new ArrayList<>();


		for(TicketView ticket:tickets) {
			TicketViewDto dto = new TicketViewDto();
			
			dto.setTicketId(ticket.getTicketId());
			dto.setOrderId(ticket.getOrderId());
			dto.setEmail(ticket.getEmail());
			dto.setPhone(ticket.getPhone());
			dto.setPrice(ticket.getPrice());
			dto.setStatus(ticket.getStatus());
			dto.setIdCard(ticket.getIdCard());
			dto.setCurrentHolderMemberId(ticket.getCurrentHolderMemberId());
			dto.setIsUsed(ticket.getIsUsed());
			dto.setParticipantName(ticket.getParticipantName());
			dto.setEventName(ticket.getEventName());
			dto.setCategoryName(ticket.getCategoryName());
			dto.setQueueId(ticket.getQueueId());
			dto.setEventFromDate(ticket.getEventFromDate());
			dto.setPlace(ticket.getPlace());
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
