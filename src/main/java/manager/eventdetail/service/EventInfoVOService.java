package manager.eventdetail.service;

import manager.eventdetail.vo.EventInfoEventVer;
import java.util.List;

public interface EventInfoVOService {
    EventInfoEventVer getEventById(Integer eventId);
    List<EventInfoEventVer> getEventsByMemberId(Integer memberId);
} 