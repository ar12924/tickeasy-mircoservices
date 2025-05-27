package manager.event.dao;

import java.util.List;

import common.dao.CommonDao;
import manager.event.vo.EventInfo2;

public interface ShowEventDao extends CommonDao {
	List<EventInfo2> showEvent();
}
