package user.ticket.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
			dto.setCurrentHolderChangeMemberId(ticket.getMemberTicketVer().getCurrentHolderChangeMemberId());
			dto.setCurrentHolderChangeUserName(ticket.getMemberTicketVer().getCurrentHolderChangeUserName());
			dto.setCurrentHolderChangeNickName(ticket.getMemberTicketVer().getCurrentHolderChangeNickName());
			dto.setCurrentHolderChangeEmail(ticket.getMemberTicketVer().getCurrentHolderChangeEmail());
			dto.setCurrentHolderChangePhone(ticket.getMemberTicketVer().getCurrentHolderChangePhone());
			dto.setCurrentHolderChangeIdCard(ticket.getMemberTicketVer().getCurrentHolderChangeIdCard());
			dto.setQrCodeHashCode(generateCustomCode(ticket.getMemberTicketVer().getCurrentHolderChangeIdCard(),ticket.getTicketId(),12));
			dto.setImage(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getImage());
			
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
				dtochange.setQrCodeHashCode(generateCustomCode(changeticket.getMemberTicketVer().getCurrentHolderChangeIdCard(),changeticket.getTicketId(),12));
				dtochange.setImage(changeticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getImage());
				
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
	
	private String generateCustomCode(String memberIdCard,int orderId,int length) {
		String MYSALT = "mySecretSalt8866";
		
	        try {
	            String input = memberIdCard + orderId + MYSALT;
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

	            // 將 byte[] 轉成可讀編碼 (0-9a-z)
	            StringBuilder sb = new StringBuilder();
	            for (byte b : hash) {
	                int val = b & 0xFF;
	                sb.append(Character.forDigit(val % 36, 36)); // 可自定進位制
	            }

	            // 截取指定長度
	            return sb.substring(0, Math.min(length, sb.length()));
	        } catch (Exception e) {
	            throw new RuntimeException("Hash failed", e);
	        }
	    }
	@Transactional
	@Override
	public TicketViewDto findByTicketId(int ticketId) {
		Ticket ticket = ticketDao.selectTicketByTicketId(ticketId);
		TicketViewDto dto= new TicketViewDto();
		
		
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
		dto.setCurrentHolderChangeMemberId(ticket.getMemberTicketVer().getCurrentHolderChangeMemberId());
		dto.setCurrentHolderChangeUserName(ticket.getMemberTicketVer().getCurrentHolderChangeUserName());
		dto.setCurrentHolderChangeNickName(ticket.getMemberTicketVer().getCurrentHolderChangeNickName());
		dto.setCurrentHolderChangeEmail(ticket.getMemberTicketVer().getCurrentHolderChangeEmail());
		dto.setCurrentHolderChangePhone(ticket.getMemberTicketVer().getCurrentHolderChangePhone());
		dto.setCurrentHolderChangeIdCard(ticket.getMemberTicketVer().getCurrentHolderChangeIdCard());
		dto.setQrCodeHashCode(generateCustomCode(ticket.getIdCard(),ticket.getTicketId(),12));
		dto.setImage(ticket.getBuyerOrderTicketVer().getEventInfoTicketVer().getImage());
		
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
		
		return dto;
	}

	@Transactional
	@Override
	public Integer updateTicketStatus(int ticketId) {
		return ticketDao.updateTicketStatus(ticketId);
	}

		
	}

	

