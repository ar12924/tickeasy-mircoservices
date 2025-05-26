package user.ticket.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.Session;

import user.notify.vo.Notification;
import user.ticket.dao.TicketDao;
import user.ticket.vo.Ticket;
import user.ticket.vo.TicketView;

public class TicketDaoImpl implements TicketDao {
	private DataSource ds;

	public TicketDaoImpl() throws NamingException {
		ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
	}

	@Override
	public List<TicketView> selectAllByMemberId(int memberId){
		List<TicketView> ticketViewList = new ArrayList<>();

		String sql = "SELECT bt.ticket_id,bt.order_id,bt.email,bt.phone,"
				+ "bt.price,bt.status,bt.id_card,bt.current_holder_member_id,"
				+ "bt.is_used,bt.participant_name,bt.event_name,ett.category_name,bt.queue_id,"
				+ "be.event_from_date,be.place,bt.create_time,bt.update_time\r\n"
				+ "FROM  buyer_ticket bt\r\n"
				+ "JOIN (SELECT ei.event_from_date,ei.place,bo.order_id FROM event_info ei JOIN buyer_order bo ON ei.event_id=bo.event_id) AS be ON be.order_id=bt.order_id\r\n"
				+ "JOIN event_ticket_type ett ON ett.type_id=bt.type_id\r\n"
				+ "WHERE bt.current_holder_member_id=?\r\n";

		try (Connection conn = ds.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {
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

		
	}

}
