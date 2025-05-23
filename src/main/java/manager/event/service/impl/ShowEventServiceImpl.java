package manager.event.service.impl;

import java.util.List;

import manager.event.dao.ShowEventDao;
import manager.event.dao.impl.ShowEventDaoImpl;
import manager.event.service.ShowEventService;
import manager.event.vo.EventInfo;

public class ShowEventServiceImpl implements ShowEventService {
	private ShowEventDao showEventDao;

	public ShowEventServiceImpl() {
		showEventDao = new ShowEventDaoImpl();
	}

	@Override
	public List<EventInfo> showEvent() {

		List<EventInfo> eventInfoLst = showEventDao.showEvent();

		return eventInfoLst;
	}
}