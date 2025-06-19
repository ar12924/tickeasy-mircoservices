package manager.eventdetail.dao;

import java.util.List;
import java.util.Map;

import common.dao.CommonDao;
import manager.eventdetail.vo.EventTicketType;

public interface TicketSalesDao extends CommonDao{
    List<EventTicketType> getEventTicketTypes(Integer eventId);
    Integer getSoldTicketCount(Integer typeId);
    Integer getUsedTicketCount(Integer typeId);
    Map<Integer, Map<String, Object>> getTicketSalesStatistics(Integer eventId);
    Map<String, Object> getSalesDashboardData(Integer eventId);
}
