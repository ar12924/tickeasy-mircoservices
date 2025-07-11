package manager.event.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.event.dao.EventDao;
import manager.event.vo.MngEventInfo;

@Repository
public class EventDaoImpl implements EventDao {
	@PersistenceContext
	private Session session;

	@Override
	public int createEvent(MngEventInfo eventInfo) {
		session.persist(eventInfo);
		return eventInfo.getEventId();
	}

	@Override
	public MngEventInfo findById(Integer eventId) {
		return session.get(MngEventInfo.class, eventId);
	}

	@Override
	public List<MngEventInfo> findAll(Integer memberId) {
		String hql = "FROM MngEventInfo WHERE memberId = :memberId ORDER BY eventId DESC";
		return session.createQuery(hql, manager.event.vo.MngEventInfo.class)
				.setParameter("memberId", memberId)
				.getResultList();
	}
	
	@Override
    public int updateEvent(MngEventInfo eventInfo) {
		try {
            System.out.println("=== 開始更新活動 ===");
            System.out.println("活動ID: " + eventInfo.getEventId());
            System.out.println("活動名稱: " + eventInfo.getEventName());
            
            // 先檢查記錄是否存在
            MngEventInfo existingEvent = session.get(MngEventInfo.class, eventInfo.getEventId());
            if (existingEvent == null) {
                System.err.println("找不到活動ID: " + eventInfo.getEventId());
                return 0;
            }
            
            // 更新非空欄位
            existingEvent.setEventName(eventInfo.getEventName());
            existingEvent.setEventFromDate(eventInfo.getEventFromDate());
            existingEvent.setEventToDate(eventInfo.getEventToDate());
            existingEvent.setEventHost(eventInfo.getEventHost());
            existingEvent.setTotalCapacity(eventInfo.getTotalCapacity());
            existingEvent.setPlace(eventInfo.getPlace());
            existingEvent.setSummary(eventInfo.getSummary());
            existingEvent.setDetail(eventInfo.getDetail());
            
            // 只在有新圖片時更新圖片
            if (eventInfo.getImage() != null) {
                existingEvent.setImage(eventInfo.getImage());
            }
            
            // 使用 merge
            session.merge(existingEvent);
            session.flush(); // 立即執行 SQL
            
            System.out.println("✅ 活動更新成功");
            return 1;
            
        } catch (Exception e) {
            System.err.println("更新活動失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

	@Override
	public int toggleEventStatus(Integer eventId, Integer isPosted) {
		try {
            System.out.println("=== 使用 HQL 更新活動狀態 ===");
            System.out.println("活動ID: " + eventId + ", 新狀態: " + isPosted);
            
            // ✅ 使用 HQL 直接更新
            String hql = "UPDATE MngEventInfo SET isPosted = :isPosted WHERE eventId = :eventId";
            int result = session.createQuery(hql)
                    .setParameter("isPosted", isPosted)
                    .setParameter("eventId", eventId)
                    .executeUpdate();
            
            System.out.println("✅ 更新影響的記錄數: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("更新活動狀態失敗: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
	}
}