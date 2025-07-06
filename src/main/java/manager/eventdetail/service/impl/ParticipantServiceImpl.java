package manager.eventdetail.service.impl;

import java.util.Collections;
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
    public Map<String, Object> getParticipantDetail(Integer ticketId) {
        BuyerTicketEventVer ticket = participantDao.getParticipantDetail(ticketId);
        if (ticket == null) {
            return Collections.singletonMap("error", "找不到指定的票券");
        }
        // 設定 QR code 內容
        ticket.setQrCodeContent("QR:" + (ticket.getTicketId() != null ? ticket.getTicketId() : ""));
        Map<String, Object> map = new HashMap<>();
        map.put("ticket", ticket); // 直接放 VO 物件
        map.put("success", true);

        // 設定報名人資料
        Map<String, Object> participantInfo = new HashMap<>();
        participantInfo.put("name", ticket.getParticipantName());
        participantInfo.put("email", ticket.getEmail());
        participantInfo.put("idCard", ticket.getIdCard());
        participantInfo.put("phone", ticket.getPhone());
        map.put("participantInfo", participantInfo);

        // 訂單時間
        if (ticket.getBuyerOrder() != null && ticket.getBuyerOrder().getOrderTime() != null) {
            map.put("orderTime", ticket.getBuyerOrder().getOrderTime());
        }

        // 訂單資訊
        Integer orderId = ticket.getOrderId();
        if (orderId != null) {
            BuyerOrderEventVer order = participantDao.getOrderInfo(orderId);
            if (order != null) {
                map.put("order", order);
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

    // 搜尋報名人
    @Override
    @Transactional
    public Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams) {
        Map<String, Object> searchResult = participantDao.searchParticipants(eventId, searchParams);
        
        String eventName = participantDao.getEventNameById(eventId);
        
        Map<String, Object> finalResult = new HashMap<>();
        
        finalResult.putAll(searchResult);
        
        finalResult.put("eventName", eventName);
        
        return finalResult;
    }

    @Override
    @Transactional
    public boolean updateTicketStatus(Integer ticketId, Integer status, Integer isUsed) {
        return participantDao.updateTicketStatus(ticketId, status, isUsed);
    }

    @Override
    @Transactional
    public List<EventTicketType> getEventTicketTypes(Integer eventId) {
        return participantDao.getEventTicketTypes(eventId);
    }

    // 取得報名人列表（分頁）
    @Override
    @Transactional
    public Map<String, Object> getParticipants(Integer eventId, int page, int pageSize) {
        return participantDao.getParticipants(eventId, page, pageSize);
    }
}