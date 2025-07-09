package manager.event.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.event.dao.EventDao;
import manager.event.dao.KeywordCategoryDao;
import manager.event.service.EventService;
import manager.event.vo.MngEventInfo;
import manager.event.vo.MngKeywordCategory;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventDao eventDao;
	
	@Autowired
	private KeywordCategoryDao keywordDao;

	@Override
	public List<MngEventInfo> findAllEvents(Integer memberId) {
		return eventDao.findAll(memberId);
	}

	@Override
	public MngEventInfo findEventById(Integer eventId) {
		return eventDao.findById(eventId);
	}

	@Transactional
	@Override
	public Integer createKeywordCategory(MngKeywordCategory kCategory) {
		return keywordDao.createKeywordCategory(kCategory);
	}

//	@Override
//	@Transactional
//	public int createEvent(MngEventInfo eventInfo) {
//		return eventDao.createEvent(eventInfo);
//	}
	@Transactional
	@Override
	public int createEvent(MngEventInfo eventInfo) {
	    return eventDao.createEvent(eventInfo); // ✅ 確保這個方法會 insert
	}

}