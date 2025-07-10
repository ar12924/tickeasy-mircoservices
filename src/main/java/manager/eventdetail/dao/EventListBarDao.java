package manager.eventdetail.dao;

import java.util.List;

import manager.eventdetail.vo.EventInfoBarVer;

public interface EventListBarDao {

	List<EventInfoBarVer> selectAllEventInfoName();
	List<EventInfoBarVer> selectMemberEventInfoName(int memberId);
	
}
