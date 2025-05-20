package manager.event.dao;

import java.util.List;

import common.dao.CommonDao;
import manager.event.vo.EventInfo;

public interface ShowEventDao extends CommonDao {
	List<EventInfo> showEvent();
}
