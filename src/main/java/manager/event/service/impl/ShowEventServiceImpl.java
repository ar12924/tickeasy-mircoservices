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
		// 2. 查詢 event_info
		List<EventInfo> eventInfoLst = showEventDao.showEvent();
		// 3. 判斷回傳資料是否為空的？
//		List<EventInfo> payload = new Payload<>();
//		if (eventInfoLst.isEmpty()) {
//			payload.setSuccessful(false);
//			payload.setMessage("查無資料");
//		} else {
//			payload.setSuccessful(true);
//			payload.setMessage("取得資料");
//		}
//		payload.setData(eventInfoLst);
		return eventInfoLst;
	}
}