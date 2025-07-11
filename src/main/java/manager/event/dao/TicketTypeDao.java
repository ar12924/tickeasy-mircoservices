package manager.event.dao;

import java.util.List;
import common.dao.CommonDao;
import manager.event.controller.TicketTypeController.EventInfo;
import manager.event.vo.EventTicketType;

public interface TicketTypeDao extends CommonDao {
    
    /**
     * 建立票種
     */
    public Integer createTicketType(EventTicketType ticketType);
    
    /**
     * 根據ID查詢票種
     */
    public EventTicketType findTicketTypeById(Integer typeId);
    
    /**
     * 根據活動ID查詢所有票種
     */
    public List<EventTicketType> findTicketTypesByEventId(Integer eventId);
    
    /**
     * 更新票種
     */
    public int updateTicketType(EventTicketType ticketType);
    
    /**
     * 刪除票種
     */
    public int deleteTicketType(Integer typeId);
    
    /**
     * 根據活動ID取得活動資訊
     */
    public EventInfo getEventInfo(Integer eventId);
}