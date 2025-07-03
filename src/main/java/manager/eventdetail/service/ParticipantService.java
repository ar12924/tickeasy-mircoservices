package manager.eventdetail.service;

import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;

import java.util.List;
import java.util.Map;

public interface ParticipantService {

    List<EventTicketType> getEventTicketTypes(Integer eventId);

    List<BuyerTicketEventVer> getParticipantList(Integer eventId);

    Map<String, Object> getParticipantDetail(Integer ticketId);

    BuyerOrderEventVer getOrderInfo(Integer orderId);

    Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams);

    boolean updateTicketStatus(Integer ticketId, Integer status, Integer isUsed);

    Map<String, Object> getSalesDashboardData(Integer eventId);

    Map<String, Object> getParticipants(Integer eventId, int page, int pageSize);

    List<EventTicketType> getTicketTypesByEventId(Integer eventId);
}