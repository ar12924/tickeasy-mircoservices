package user.buy.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;
import user.buy.vo.BookEventDto;
import user.buy.vo.BookTypeDto;
import user.member.vo.Member;

@Repository
public class BookDaoImpl implements BookDao {

	@PersistenceContext
	private Session session;

	/**
	 * 透過活動 id，查詢票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookTypeDto>} 活動 id 下的票種資訊。
	 */
	@Override
	public List<BookTypeDto> selectTypeById(Integer eventId) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookTypeDto(");
		hqlTemp.append("typeId, categoryName, price, capacity) ");
		hqlTemp.append("FROM EventTicketType ");
		hqlTemp.append("WHERE eventId = :eventId");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<BookTypeDto> query = session.createQuery(hql, BookTypeDto.class);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}
	
	/**
	 * 透過活動 id，查詢活動資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookEventDto} 活動 id 下的活動資訊。
	 */
	@Override
	public BookEventDto selectEventById(Integer eventId) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookEventDto(");
		hqlTemp.append("eventId, eventName, isPosted) ");
		hqlTemp.append("FROM EventInfo ");
		hqlTemp.append("WHERE eventId = :eventId");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<BookEventDto> query = session.createQuery(hql, BookEventDto.class);
		query.setParameter("eventId", eventId);
		return query.uniqueResult();
	}
	
	/**
	 * 透過 userName 查詢購票人(Member)。
	 * 
	 * @param {String} userName - 購票人 userName。
	 * @return {Member} 購票人。
	 */
	@Override
	public Member selectMemberByUserName(String userName) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("From Member m ");
		hqlTemp.append("WHERE m.userName = :userName");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<Member> query = session.createQuery(hql, Member.class);
		query.setParameter("userName", userName);
		return query.uniqueResult();
	}
	
	/**
	 * 透過 idCard 查詢購票人(Member)。
	 * 
	 * @param {String} idCard - 購票人 idCard。
	 * @return {Member} 購票人。
	 */
	@Override
	public Member selectMemberByIdCard(String idCard) {
		
		// 1. HQL 語句
		var hqlTemp = new StringBuilder("From Member m ");
		hqlTemp.append("WHERE m.idCard = :idCard");
		var hql = hqlTemp.toString();
		
		// 2. 透過 eventId 查詢 type
		Query<Member> query = session.createQuery(hql, Member.class);
		query.setParameter("idCard", idCard);
		return query.uniqueResult();
	}

}
