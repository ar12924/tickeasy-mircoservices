package manager.eventdetail.dao;

import java.util.List;
import manager.eventdetail.vo.EventInfoEventVer;

public interface EventInfoVODao {
    EventInfoEventVer getEventById(Integer eventId);
    List<EventInfoEventVer> getEventsByMemberId(Integer memberId);
} 