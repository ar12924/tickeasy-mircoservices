package manager.eventdetail.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import manager.eventdetail.dao.EventListBarDao;
import manager.eventdetail.vo.EventInfoBarVer;


@Repository
public class EventListBarDaoImpl implements EventListBarDao{

	@PersistenceContext
	private Session session;
	
	@Override
	public List<EventInfoBarVer> selectAllEventInfoName() {
		List<EventInfoBarVer> eventInfoNameList = new ArrayList<>();
		String hql = "FROM EventInfoBarVer";
		eventInfoNameList = session
				.createQuery(hql, EventInfoBarVer.class)
				.getResultList();
		System.out.println("查到資料筆數：" + eventInfoNameList.size());
		return eventInfoNameList;
	}

	@Override
	public List<EventInfoBarVer> selectMemberEventInfoName(int memberId) {
		List<EventInfoBarVer> eventInfoNameList = new ArrayList<>();
		String hql = "FROM EventInfoBarVer WHERE memberId=:memberId";
		eventInfoNameList = session
				.createQuery(hql, EventInfoBarVer.class)
				.setParameter("memberId", memberId)
				.getResultList();
		System.out.println("查到資料筆數：" + eventInfoNameList.size());
		return eventInfoNameList;
	}

}
