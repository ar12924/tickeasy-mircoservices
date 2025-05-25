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

public class TicketDaoImpl implements TicketDao {
	private DataSource ds;

	public TicketDaoImpl() throws NamingException {
		ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
	}

	@Override
	public List<Ticket> selectAllByMemberId(int memberId){
		List<Ticket> ticketList = new ArrayList<>();

		String sql = "SELECT * FROM  buyer_ticket WHERE current_holder_member_id=?";

		try (Connection conn = ds.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setInt(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Ticket ticket = new Ticket();
					ticket.setTicketId(rs.getInt("ticket_id"));
					ticket.setOrderId(rs.getInt("order_id"));
					ticket.setEmail(rs.getString("email"));
					ticket.setPhone(rs.getString("phone"));
					ticket.setPrice(rs.getDouble("price"));
					ticket.setStatus(rs.getInt("status"));
					ticket.setIdCard(rs.getString("id_card"));
					ticket.setCurrentHolderMemberId(rs.getInt("current_holder_member_id"));
					ticket.setIsUsed(rs.getInt("is_used"));
					ticket.setParticipantName(rs.getString("participant_name"));
					ticket.setEventName(rs.getString("event_name"));
					ticket.setTypeId(rs.getInt("type_id"));
					ticket.setQueueId(rs.getInt("queue_id"));
					ticket.setCreateTime(rs.getTimestamp("create_time"));
					ticket.setUpdateTime(rs.getTimestamp("update_time"));
					ticketList.add(ticket);

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("查到資料筆數：" + ticketList.size());
		return ticketList;

		
	}

}
