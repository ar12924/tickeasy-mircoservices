package manager.event.service;

import java.util.List;
import manager.event.vo.EventTicketType;
import manager.event.controller.TicketTypeController.EventInfo;

public interface TicketTypeService {
    
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