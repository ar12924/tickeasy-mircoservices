package manager.eventdetail.dao;

import common.dao.CommonDao;
import manager.eventdetail.vo.EventTicketType;

import java.util.List;
import java.util.Map;

public interface TicketSalesDao extends CommonDao{
    List<EventTicketType> getEventTicketTypes(Integer eventId);
    Integer getSoldTicketCount(Integer typeId);
    Integer getSoldTicketCountByEventId(Integer eventId);
    // 新增：銷售趨勢查詢
    List<Map<String, Object>> getSalesTrend(Integer eventId);
    // 票種分布趨勢查詢
    List<Object[]> findTicketTypeTrendByEventId(Integer eventId);
}
