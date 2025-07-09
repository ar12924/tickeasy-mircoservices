package manager.eventdetail.dao;

import common.dao.CommonDao;
import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;

import java.util.List;
import java.util.Map;

public interface ParticipantDao extends CommonDao {

    List<BuyerTicketEventVer> getParticipantList(Integer eventId);

    BuyerTicketEventVer getParticipantDetail(Integer ticketId);

    BuyerOrderEventVer getOrderInfo(Integer orderId);

    int countTicketsByOrderId(Integer orderId);

    Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams);

    List<EventTicketType> getEventTicketTypes(Integer eventId);

    boolean updateTicketStatus(Integer ticketId, Integer status, Integer isUsed);

    String getEventNameById(Integer eventId);

    Map<String, Object> getParticipants(Integer eventId, int page, int pageSize);
}
