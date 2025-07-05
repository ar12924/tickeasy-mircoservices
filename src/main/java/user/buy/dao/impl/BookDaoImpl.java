package user.buy.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import user.buy.dao.BookDao;
import user.buy.vo.BookTypeDto;
import user.buy.vo.BuyerOrder;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.member.vo.Member;

@Repository
public class BookDaoImpl implements BookDao {

	@PersistenceContext
	private Session session;

	/**
	 * 透過活動 id，查詢所有票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {List<BookTypeDto>} 活動 id 下的票種資訊。
	 */
	@Override
	public List<BookTypeDto> selectAllTypeById(Integer eventId) {

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
	 * 透過 (活動 id ,票種 id)，查詢單一票種資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @param {Integer} typeId - 票種 id。
	 * @return {List<BookTypeDto>} 單一票種資訊。
	 */
	@Override
	public BookTypeDto selectTypeById(Integer eventId, Integer typeId) {

		// 1. HQL 語句
		var hqlTemp = new StringBuilder("SELECT new user.buy.vo.BookTypeDto(");
		hqlTemp.append("typeId, categoryName, price, capacity) ");
		hqlTemp.append("FROM EventTicketType ");
		hqlTemp.append("WHERE eventId = :eventId AND typeId = :typeId");
		var hql = hqlTemp.toString();

		// 2. 透過 eventId 查詢 type
		Query<BookTypeDto> query = session.createQuery(hql, BookTypeDto.class);
		query.setParameter("eventId", eventId);
		query.setParameter("typeId", typeId);
		return query.uniqueResult();
	}

	/**
	 * 透過活動 id，查詢活動資訊。
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @return {BookEventDto} 活動 id 下的活動資訊。
	 */
	@Override
	public EventInfo selectEventById(Integer eventId) {

		// 1. HQL 語句
		var hqlTemp = new StringBuilder("FROM EventInfo ");
		hqlTemp.append("WHERE eventId = :eventId");
		var hql = hqlTemp.toString();

		// 2. 透過 eventId 查詢 type
		Query<EventInfo> query = session.createQuery(hql, EventInfo.class);
		query.setParameter("eventId", eventId);
		return query.uniqueResult();
	}

	/**
	 * 透過 userName 查詢會員(購票人或入場者)。
	 * 
	 * @param {String} userName - 會員 userName。
	 * @return {Member} 會員資料。
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
	 * 將 BookDto 基本資料 (eventId, memberId, isPaid) 存入 buyer_order 表
	 * 
	 * @param {Integer} eventId - 活動 id。
	 * @param {Integer} memberId - 會員 id。
	 * @param {Integer} isPaid - 付款與否。
	 * @return {Integer} 插入後生成的訂單 id。
	 */
	@Override
	public Integer insertBuyerOrderAndGetId(Integer eventId, Integer memberId, Integer isPaid, BigDecimal totalAmount) {
		// 1. 將資料 (eventId, memberId, isPaid) 放入實體物件
		var order = new BuyerOrder();
		order.setEventId(eventId);
		order.setMemberId(memberId);
		order.setIsPaid(isPaid);
		order.setOrderStatus("已付款");
		order.setOrderTime(new Timestamp(System.currentTimeMillis()));
		order.setTotalAmount(totalAmount);
		
		// 2. 插入新的一筆訂單
		session.persist(order);
		session.flush(); // 強制執行 SQL，生成 id

		// 3. 取得自動生成的 id
		return order.getOrderId();
	}

	/**
	 * 將 BookDto 入場者資料 (newOrderId, member_id, email, phone, id_card, nick_name,
	 * event_name, type_id, price, is_used) 存入 buyer_ticket 表
	 * 
	 * @param {Integer}   newOrderId - 生成的訂單 id。
	 * @param {Member}    member - 會員物件。
	 * @param {EventInfo} eventInfo - 活動物件。
	 * @return {Integer} 插入後生成的票券 id。
	 */
	@Override
	public Integer insertBuyerTicketAndGetId(Integer newOrderId, Member member, String eventName,
			BookTypeDto bookTypeDto) {
		// 1. 從 Member 取出欄位值
		Integer memberId = member.getMemberId();
		String email = member.getEmail();
		String phone = member.getPhone();
		String idCard = member.getIdCard();
		String nickName = member.getNickName();
		Integer isUsed = 0; // 剛購買尚未使用

		// 2. 從 bookTypeDto 取出欄位值
		Integer typeId = bookTypeDto.getTypeId();
		BigDecimal price = bookTypeDto.getPrice();

		// 3. 將資料 (newOrderId, memberId, email, phone, idCard, nickName,
		// eventName,
		// typeId, price) 放入 ticket 實體物件
		var ticket = new BuyerTicket();
		ticket.setOrderId(newOrderId);
		ticket.setCurrentHolderMemberId(memberId);
		ticket.setEmail(email);
		ticket.setPhone(phone);
		ticket.setIdCard(idCard);
		ticket.setParticipantName(nickName);
		ticket.setIsUsed(isUsed);
		ticket.setEventName(eventName);
		ticket.setTypeId(typeId);
		ticket.setPrice(price);
		ticket.setStatus(1);
		ticket.setQueneId(101);

		// 4. 插入新的一筆訂單
		session.persist(ticket);
		session.flush(); // 強制執行 SQL，生成 id

		// 4. 取得自動生成的 id
		return ticket.getTicketId();
	}

}
