package manager.event.service;

import java.util.List;

import manager.event.vo.MngEventInfo;
import manager.event.vo.MngKeywordCategory;

public interface EventService {

	// index.html 顯示所有活動
	public List<MngEventInfo> findAllEvents(Integer memberId);

	// 查詢活動
	public MngEventInfo findEventById(Integer eventId);

	// 建立keyword
	public Integer createKeywordCategory(MngKeywordCategory kCategory);

	// 建立活動
	public int createEvent(MngEventInfo eventInfo);

	// 更新活動
	public int updateEvent(MngEventInfo eventInfo);
	
	// 切換活動狀態
    public int toggleEventStatus(Integer eventId, Integer isPosted);
}
