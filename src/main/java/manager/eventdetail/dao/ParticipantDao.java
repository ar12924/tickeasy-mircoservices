package manager.eventdetail.dao;

import java.util.List;
import java.util.Map;
import common.dao.CommonDao;
import manager.eventdetail.vo.BuyerOrderEventVer;
import manager.eventdetail.vo.BuyerTicketEventVer;
import manager.eventdetail.vo.EventTicketType;

public interface ParticipantDao extends CommonDao {

    List<BuyerTicketEventVer> getParticipantList(Integer eventId);

    BuyerTicketEventVer getParticipantDetail(Long ticketId);

    BuyerOrderEventVer getOrderInfo(Integer orderId);

    int countTicketsByOrderId(Integer orderId);

    Map<String, Object> searchParticipants(Integer eventId, Map<String, Object> searchParams);

    List<EventTicketType> getEventTicketTypes(Integer eventId);

    boolean updateTicketStatus(Long ticketId, Integer status, Integer isUsed);

    String getEventNameById(Integer eventId);
}
