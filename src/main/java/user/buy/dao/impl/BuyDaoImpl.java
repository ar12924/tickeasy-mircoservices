package user.buy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import user.buy.dao.BuyDao;
import user.buy.vo.BuyerTicket;
import user.buy.vo.EventInfo;
import user.buy.vo.MemberNotification;

public class BuyDaoImpl implements BuyDao {
	private DataSource ds;

	public BuyDaoImpl() {
		try {
			// 取得 ds (所有 CRUD 共用)
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<EventInfo> selectEventByKeyword(String keywords) {
		// 1. 將 keywords 插入 SQL 語句進行條件搜尋
		StringBuilder sqlTemp = new StringBuilder("SELECT * FROM event_info WHERE event_name ");
		sqlTemp.append("LIKE '%");
		sqlTemp.append(keywords);
		sqlTemp.append("%' ORDER BY event_from_date");
		String sql = sqlTemp.toString();

		try ( // 2. 建立連線
				Connection conn = ds.getConnection();
				// 3. 創建預備 sql 敘述
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			// 4. 取得 rs 物件，並遍歷每筆資料
			try (ResultSet rs = pstmt.executeQuery()) {
				List<EventInfo> eventInfoLst = new ArrayList<>();
				while (rs.next()) {
					EventInfo eventInfo = new EventInfo();
					// 5. 將資料放入 vo
					eventInfo.setEvent_id(rs.getInt("event_id"));
					eventInfo.setEvent_name(rs.getString("event_name"));
					eventInfo.setEvent_from_date(rs.getTimestamp("event_from_date"));
					eventInfo.setEvent_to_date(rs.getTimestamp("event_to_date"));
					eventInfo.setEvent_host(rs.getString("event_host"));
					eventInfo.setTotal_capacity(rs.getInt("total_capacity"));
					eventInfo.setPlace(rs.getString("place"));
					eventInfo.setSummary(rs.getString("summary"));
					eventInfo.setDetail(rs.getString("detail"));
					eventInfo.setImage_dir(rs.getString("image_dir"));
					eventInfo.setImage(rs.getObject("image"));
					eventInfo.setKeyword_id(rs.getInt("keyword_id"));
					eventInfo.setCreate_time(rs.getTimestamp("create_time"));
					eventInfo.setUpdate_time(rs.getTimestamp("update_time"));
					eventInfoLst.add(eventInfo);
				}
				// 6. 回傳 list
				return eventInfoLst;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BuyerTicket> selectTicket() {
		// 1. 生成 SQL 語句進行搜尋
		String sql = "SELECT * FROM buyer_ticket;";

		try ( // 2. 建立連線
				Connection conn = ds.getConnection();
				// 3. 創建預備 sql 敘述
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			// 4. 取得 rs 物件，並遍歷每筆資料
			try (ResultSet rs = pstmt.executeQuery()) {
				List<BuyerTicket> buyerTicketLst = new ArrayList<>();
				while (rs.next()) {
					BuyerTicket buyerTicket = new BuyerTicket();
					// 5. 將資料放入 vo
					buyerTicket.setTicket_id(rs.getInt("ticket_id"));
					buyerTicket.setOrder_id(rs.getInt("order_id"));
					buyerTicket.setEmail(rs.getString("email"));
					buyerTicket.setPhone(rs.getString("phone"));
					buyerTicket.setPrice(rs.getBigDecimal("price"));
					buyerTicket.setStatus(rs.getInt("status"));
					buyerTicket.setId_card(rs.getString("id_card"));
					buyerTicket.setCurrent_holder_member_id(rs.getInt("current_holder_member_id"));
					buyerTicket.setIs_used(rs.getInt("is_used"));
					buyerTicket.setParticipant_name(rs.getString("participant_name"));
					buyerTicket.setEvent_name(rs.getString("event_name"));
					buyerTicket.setType_id(rs.getInt("type_id"));
					buyerTicket.setQuene_id(rs.getInt("queue_id"));
					buyerTicket.setCreate_time(rs.getTimestamp("create_time"));
					buyerTicket.setUpdate_time(rs.getTimestamp("update_time"));
					buyerTicketLst.add(buyerTicket);
				}
				// 6. 回傳 list
				return buyerTicketLst;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<MemberNotification> selectNotification() {
		// 1. 生成 SQL 語句進行搜尋
		String sql = "SELECT * FROM member_notification ORDER BY send_time;";

		try ( // 2. 建立連線
				Connection conn = ds.getConnection();
				// 3. 創建預備 sql 敘述
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			// 4. 取得 rs 物件，並遍歷每筆資料
			try (ResultSet rs = pstmt.executeQuery()) {
				List<MemberNotification> memberNotificationLst = new ArrayList<>();
				while (rs.next()) {
					MemberNotification memberNotification = new MemberNotification();
					// 5. 將資料放入 vo
					memberNotification.setMember_notification_id(rs.getInt("member_notification_id"));
					memberNotification.setNotification_id(rs.getInt("notification_id"));
					memberNotification.setMember_id(rs.getInt("member_id"));
					memberNotification.setIs_read(rs.getInt("is_read"));
					memberNotification.setIs_visible(rs.getInt("is_visible"));
					memberNotification.setNotification_status(rs.getInt("notification_status"));
					memberNotification.setTitle(rs.getString("title"));
					memberNotification.setMessage(rs.getString("message"));
					memberNotification.setLink_url(rs.getString("link_url"));
					memberNotification.setRead_time(rs.getTimestamp("read_time"));
					memberNotification.setSend_time(rs.getTimestamp("send_time"));
					memberNotification.setCreate_time(rs.getTimestamp("create_time"));
					memberNotification.setUpdate_time(rs.getTimestamp("update_time"));
					memberNotificationLst.add(memberNotification);
				}
				// 6. 回傳 list
				return memberNotificationLst;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
