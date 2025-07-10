package manager.event.dao;

import java.util.List;

import common.dao.CommonDao;
import manager.event.vo.MngEventInfo;

public interface EventDao extends CommonDao {

	public int createEvent(MngEventInfo eventInfo);

	public MngEventInfo findById(Integer eventId);

	public List<MngEventInfo> findAll(Integer memberId);

    public int updateEvent(MngEventInfo eventInfo);
    
    public int toggleEventStatus(Integer eventId, Integer isPosted);
}
