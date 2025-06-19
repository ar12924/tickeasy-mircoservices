package manager.eventdetail.dao;

import manager.eventdetail.vo.EventInfoEventVer;

public interface EventInfoVODao {
    EventInfoEventVer getEventById(Integer eventId);
} 