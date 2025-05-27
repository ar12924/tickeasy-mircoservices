package manager.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import manager.event.dao.ShowEventDao;
import manager.event.dao.impl.ShowEventDaoImpl;
import manager.event.service.ShowEventService;
import manager.event.vo.EventInfo2;

@Service
public class ShowEventServiceImpl implements ShowEventService {

	@Autowired
	private ShowEventDao showEventDao;

//	public ShowEventServiceImpl() {
//		showEventDao = new ShowEventDaoImpl();
//	}

	@Override
	public List<EventInfo2> showEvent() {

		List<EventInfo2> eventInfoLst = showEventDao.showEvent();

		return eventInfoLst;
	}
}