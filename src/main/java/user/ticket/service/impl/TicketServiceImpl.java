package user.ticket.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
			if(ticket.getStatus() != null && ticket.getStatus()==1) {
				dto.setStatusText("已付款");
			}else {
				dto.setStatusText("未付款");
			}
			if(ticket.getIsUsed()==1) {
				dto.setIsUsedText("已使用");
			}else {
				dto.setIsUsedText("未使用");
			}
			
			
			Date now = new Date();
			//前端頁面狀態分類
			/*if(ticket.getBuyerOrderTicketVer().getMemberId()!=ticket.getCurrentHolderMemberId()) {
				dto.setViewCategoryType(3);
			}else*/ if(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate().before(now) /*&& ticket.getBuyerOrderTicketVer().getMemberId()==ticket.getCurrentHolderMemberId()*/) {
				//歷史票
				dto.setViewCategoryType(2);
			}else if(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate().after(now) /*&& ticket.getBuyerOrderTicketVer().getMemberId()==ticket.getCurrentHolderMemberId()*/){
				//即將到來
				dto.setViewCategoryType(1);
			}
			
			result.add(dto);
			
		}
			
			//已轉讓票種
			
			List<Ticket> changetickets = ticketDao.selectAllChangeByMemberId(memberId);
			List<TicketViewDto> changeresult= new ArrayList<>();


			for(Ticket changeticket:changetickets) {
				TicketViewDto dtochange = new TicketViewDto();
				
				dtochange.setTicketId(changeticket.getTicketId());
				dtochange.setOrderId(changeticket.getOrderId());
				dtochange.setEmail(changeticket.getEmail());
				dtochange.setPhone(changeticket.getPhone());
				dtochange.setPrice(changeticket.getPrice());
				dtochange.setStatus(changeticket.getStatus());
				dtochange.setIdCard(changeticket.getIdCard());
				dtochange.setCurrentHolderMemberId(changeticket.getCurrentHolderMemberId());
				dtochange.setMemberId(changeticket.getBuyerOrderTicketVer().getMemberId());
				dtochange.setIsUsed(changeticket.getIsUsed());
				dtochange.setParticipantName(changeticket.getParticipantName());
				dtochange.setEventName(changeticket.getEventName());
				dtochange.setCategoryName(changeticket.getEventTicketTypeTicketVer().getCategoryName());
				dtochange.setQueueId(changeticket.getQueueId());
				dtochange.setEventFromDate(changeticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate());
				dtochange.setPlace(changeticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getPlace());
				dtochange.setCreateTime(changeticket.getCreateTime());
				dtochange.setUpdateTime(changeticket.getUpdateTime());
				
				
				//狀態轉換邏輯
				if(changeticket.getStatus() != null && changeticket.getStatus()==1) {
					dtochange.setStatusText("已付款");
				}else {
					dtochange.setStatusText("未付款");
				}
				if(changeticket.getIsUsed()==1) {
					dtochange.setIsUsedText("已使用");
				}else {
					dtochange.setIsUsedText("未使用");
				}
				
				
				
				//前端頁面狀態分類
				if(changeticket.getBuyerOrderTicketVer().getMemberId()!=changeticket.getCurrentHolderMemberId()) {
					dtochange.setViewCategoryType(3);
				}/*else if(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate().before(now) && ticket.getBuyerOrderTicketVer().getMemberId()==ticket.getCurrentHolderMemberId()) {
					//歷史票
					dto.setViewCategoryType(2);
				}else if(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getEventFromDate().after(now) && ticket.getBuyerOrderTicketVer().getMemberId()==ticket.getCurrentHolderMemberId()){
					//即將到來
					dto.setViewCategoryType(1);
				}*/
				
				result.add(dtochange);
		}
		
		
		
		System.out.println(result);
		
		return result;
		
	}

	
}
