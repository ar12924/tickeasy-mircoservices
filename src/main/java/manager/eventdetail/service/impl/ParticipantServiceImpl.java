package manager.eventdetail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.eventdetail.dao.ParticipantDao;
import manager.eventdetail.dao.TicketSalesDao;
import manager.eventdetail.service.ParticipantService;
import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    
    @Autowired
    private ParticipantDao participantDao;

    @Autowired
    private TicketSalesDao ticketSalesDao;

    // 報名人列表
    @Override
    @Transactional
    public List<BuyerTicketEventVer> getParticipantList(Integer eventId) {
        List<BuyerTicketEventVer> list = participantDao.getParticipantList(eventId);
        int seq = 1;
        for (BuyerTicketEventVer ticket : list) {
            if (ticket.getQueueId() == null) ticket.setQueueId(seq++);
            ticket.setQrCodeContent("QR:" + (ticket.getTicketId() != null ? ticket.getTicketId() : ""));
        }
        return list;
    }

    @Override
    @Transactional
    public Map<String, Object> getParticipantDetail(Long ticketId) {
        Map<String, Object> map = new HashMap<>();
        BuyerTicketEventVer ticket = participantDao.getParticipantDetail(ticketId);
        if (ticket == null)  {
            map.put("success", false);
            map.put("msg", "查無資料");
            return map;
        }
        
        // 設定 QR code 內容
        ticket.setQrCodeContent("QR:" + (ticket.getTicketId() != null ? ticket.getTicketId() : ""));
        map.put("ticket", ticket);
        map.put("success", true);
    
        // 設定報名人資料
        Map<String, Object> participantInfo = new HashMap<>();
        participantInfo.put("name", ticket.getParticipantName());
        participantInfo.put("email", ticket.getEmail());
        participantInfo.put("idCard", ticket.getIdCard());
        participantInfo.put("phone", ticket.getPhone());
        map.put("participantInfo", participantInfo);
    
        // 設置訂單時間
        if (ticket.getBuyerOrder() != null && ticket.getBuyerOrder().getOrderTime() != null) {
            ticket.setOrderTime(ticket.getBuyerOrder().getOrderTime().toLocalDateTime());
            map.put("orderTime", ticket.getOrderTime());
        }
    
        // 訂單資訊
        Integer orderId = ticket.getOrderId();
        if (orderId != null) {
            BuyerOrderEventVer order = participantDao.getOrderInfo(orderId);
            if (order != null) {
                map.put("order", order);
                // 查詢該訂單下所有票券數量
                int ticketQuantity = participantDao.countTicketsByOrderId(orderId);
                map.put("ticketQuantity", ticketQuantity);
            }
        }
        return map;
    }


    @Override
    @Transactional
    public BuyerOrderEventVer getOrderInfo(Integer orderId) {
        return participantDao.getOrderInfo(orderId);
    }

    @Override
    @Transactional
    public Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams) {
        // First, get the search results (participants and total count) from DAO
        Map<String, Object> searchResult = participantDao.searchParticipants(eventId, searchParams);
        
        // Then, get the event name from DAO
        String eventName = participantDao.getEventNameById(eventId);
        
        // Create a new map to hold the final result
        Map<String, Object> finalResult = new HashMap<>();
        
        // Put all search results (which should include 'participants' and 'totalCount') into the final map
        finalResult.putAll(searchResult);
        
        // Add the event name to the final map
        finalResult.put("eventName", eventName);
        
        return finalResult;
    }

    @Override
    @Transactional
    public boolean updateTicketStatus(Long ticketId, Integer status, Integer isUsed) {
        return participantDao.updateTicketStatus(ticketId, status, isUsed);
    }

    @Override
    @Transactional
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        return participantDao.getEventTicketTypes(eventId);
    }

    @Override
    @Transactional
    public Map<String, Object> getSalesDashboardData(Integer eventId) {
        Map<String, Object> dashboardData = ticketSalesDao.getSalesDashboardData(eventId);
        String eventName = participantDao.getEventNameById(eventId);
        dashboardData.put("eventName", eventName);
        return dashboardData;
    }
}