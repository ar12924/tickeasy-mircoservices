package user.ticket.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.ticket.dao.TicketDao;
import user.ticket.vo.Ticket;


@Repository
public class TicketDaoImpl implements TicketDao {
	
	@PersistenceContext
	private Session session;
	/*
	 * private DataSource ds;
	 * 
	 * public TicketDaoImpl() throws NamingException { ds = (DataSource) new
	 * InitialContext().lookup("java:comp/env/jdbc/tickeasy"); }
	 */

	@Override
	public List<Ticket> selectAllByMemberId(int memberId){
		
		
	
		
		List<Ticket> ticketList = new ArrayList<>();

		
		String hql = "SELECT tt FROM Ticket tt JOIN FETCH tt.buyerOrderTicketVer botv\r\n"
				+ "JOIN FETCH botv.eventInfoTicketVer\r\n"
				+ "JOIN FETCH tt.eventTicketTypeTicketVer\r\n"
				+ "JOIN FETCH tt.memberTicketVer\r\n"
				+ "WHERE tt.currentHolderMemberId=:memberId";
				
				/*+ "SELECt bt.ticket_id,bt.order_id,bt.email,bt.phone,"
				+ "bt.price,bt.status,bt.id_card,bt.current_holder_member_id,"
				+ "bt.is_used,bt.participant_name,bt.event_name,ett.category_name,bt.queue_id,"
				+ "be.event_from_date,be.place,be.member_id,bt.create_time,bt.update_time\r\n"
				+ "FROM  buyer_ticket bt\r\n"
				+ "JOIN (SELECT ei.event_from_date,ei.place,bo.order_id,bo.member_id\r\n "
				+ "FROM event_info ei JOIN buyer_order bo ON ei.event_id=bo.event_id) AS be ON be.order_id=bt.order_id\r\n"
				+ "JOIN event_ticket_type ett ON ett.type_id=bt.type_id\r\n"
				+ "WHERE bt.current_holder_member_id=?\r\n";*/
		
				ticketList = session
				.createQuery(hql, Ticket.class)
				.setParameter("memberId", memberId)
				.getResultList();
		return ticketList;
		
	

		
		/*ticketViewList = session.createNativeQuery(sql, TicketView.class).setParameter(1, memberId)
				.getResultList();

		System.out.println("查到資料筆數：" + ticketViewList.size());
		return ticketViewList;*/
		/*
		try (Connection conn = session.getConnection(); 
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setInt(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					TicketView ticketView = new TicketView();
					ticketView.setTicketId(rs.getInt("ticket_id"));
					ticketView.setOrderId(rs.getInt("order_id"));
					ticketView.setEmail(rs.getString("email"));
					ticketView.setPhone(rs.getString("phone"));
					ticketView.setPrice(rs.getDouble("price"));
					ticketView.setStatus(rs.getInt("status"));
					ticketView.setIdCard(rs.getString("id_card"));
					ticketView.setCurrentHolderMemberId(rs.getInt("current_holder_member_id"));
					ticketView.setMemberId(rs.getInt("member_id"));
					ticketView.setIsUsed(rs.getInt("is_used"));
					ticketView.setParticipantName(rs.getString("participant_name"));
					ticketView.setEventName(rs.getString("event_name"));
					ticketView.setCategoryName(rs.getString("category_name"));
					ticketView.setQueueId(rs.getInt("queue_id"));
					ticketView.setEventFromDate(rs.getTimestamp("event_from_date"));
					ticketView.setPlace(rs.getString("place"));
					ticketView.setCreateTime(rs.getTimestamp("create_time"));
					ticketView.setUpdateTime(rs.getTimestamp("update_time"));
					
					ticketViewList.add(ticketView);

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("查到資料筆數：" + ticketViewList.size());
		return ticketViewList;

		*/
	}

	@Override
	public List<Ticket> selectAllChangeByMemberId(int memberId) {

		List<Ticket> ticketList = new ArrayList<>();

		
		String hql = "SELECT tt FROM Ticket tt JOIN FETCH tt.buyerOrderTicketVer botv\r\n"
				+ "JOIN FETCH botv.eventInfoTicketVer\r\n"
				+ "JOIN FETCH tt.eventTicketTypeTicketVer\r\n"
				+ "WHERE tt.buyerOrderTicketVer.memberId=:memberId AND tt.buyerOrderTicketVer.memberId != tt.currentHolderMemberId";
				
				/*+ "SELECt bt.ticket_id,bt.order_id,bt.email,bt.phone,"
				+ "bt.price,bt.status,bt.id_card,bt.current_holder_member_id,"
				+ "bt.is_used,bt.participant_name,bt.event_name,ett.category_name,bt.queue_id,"
				+ "be.event_from_date,be.place,be.member_id,bt.create_time,bt.update_time\r\n"
				+ "FROM  buyer_ticket bt\r\n"
				+ "JOIN (SELECT ei.event_from_date,ei.place,bo.order_id,bo.member_id\r\n "
				+ "FROM event_info ei JOIN buyer_order bo ON ei.event_id=bo.event_id) AS be ON be.order_id=bt.order_id\r\n"
				+ "JOIN event_ticket_type ett ON ett.type_id=bt.type_id\r\n"
				+ "WHERE bt.current_holder_member_id=?\r\n";*/
		
				ticketList = session
				.createQuery(hql, Ticket.class)
				.setParameter("memberId", memberId)
				.getResultList();
				
		return ticketList;
		
	
	}

	@Override
	public Ticket selectTicketByTicketId(int ticketId) {
		Ticket ticket = new Ticket();

		
		String hql = "SELECT tt FROM Ticket tt JOIN FETCH tt.buyerOrderTicketVer botv\r\n"
				+ "JOIN FETCH botv.eventInfoTicketVer\r\n"
				+ "JOIN FETCH tt.eventTicketTypeTicketVer\r\n"
				+ "JOIN FETCH tt.memberTicketVer\r\n"
				+ "WHERE tt.ticketId=:ticketId";
	
		
				ticket = session
				.createQuery(hql, Ticket.class)
				.setParameter("ticketId", ticketId)
				.getSingleResult();
		return ticket;
	}

	@Override
	public Integer updateTicketStatus(int ticketId) {
		Ticket ticket = new Ticket();
		String hql = "UPDATE Ticket tt SET tt.isUsed = 1, tt.updateTime = :updateTime WHERE tt.ticketId = :ticketId";
	
		
			
		return session
				.createQuery(hql)
				.setParameter("ticketId", ticketId)
				.setParameter("updateTime",java.sql.Timestamp.valueOf(LocalDateTime.now()))
				.executeUpdate();
		
	}

}
