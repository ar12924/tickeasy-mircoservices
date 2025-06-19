package manager.eventdetail.service;

import manager.eventdetail.vo.EventInfoEventVer;

public interface EventInfoVOService {
    EventInfoEventVer getEventById(Integer eventId);
} 