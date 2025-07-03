package manager.eventdetail.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.eventdetail.dao.EventListBarDao;
import manager.eventdetail.service.EventListBarService;
import manager.eventdetail.vo.EventInfoBarVer;


@Service
public class EventListBarServiceImpl implements EventListBarService{

	@Autowired
	private EventListBarDao eventListBarDao;
	
	@Transactional
	@Override
	public List<EventInfoBarVer> eventListBar() {
		return eventListBarDao.selectAllEventInfoName();
	}

}
