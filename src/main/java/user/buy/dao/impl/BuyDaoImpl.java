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

import common.util.HibernateUtil5;
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
	public List<EventInfo> selectEventByKeyword(String keyword) {
		// 1. 將 keywords 插入 SQL 語句進行條件搜尋
		StringBuilder sqlTemp = new StringBuilder("SELECT * FROM event_info WHERE event_name ");
		sqlTemp.append("LIKE '%");
		sqlTemp.append(keyword);
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
					eventInfo.setEventId(rs.getInt("event_id"));
					eventInfo.setEventName(rs.getString("event_name"));
					eventInfo.setEventFromDate(rs.getTimestamp("event_from_date"));
					eventInfo.setEventToDate(rs.getTimestamp("event_to_date"));
					eventInfo.setEventHost(rs.getString("event_host"));
					eventInfo.setTotalCapacity(rs.getInt("total_capacity"));
					eventInfo.setPlace(rs.getString("place"));
					eventInfo.setSummary(rs.getString("summary"));
					eventInfo.setDetail(rs.getString("detail"));
					eventInfo.setImageDir(rs.getString("image_dir"));
					eventInfo.setImage(rs.getObject("image"));
					eventInfo.setKeywordId(rs.getInt("keyword_id"));
					eventInfo.setCreateTime(rs.getTimestamp("create_time"));
					eventInfo.setUpdateTime(rs.getTimestamp("update_time"));
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
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM BuyerTicket";
		// 2. 查詢所有 row
		return getSession().createQuery(hql, BuyerTicket.class).getResultList();
	}

	@Override
	public List<MemberNotification> selectNotification() {
		// 1. 生成 HQL 語句進行搜尋
		String hql = "FROM MemberNotification ORDER BY sendTime";
		return getSession().createQuery(hql, MemberNotification.class).getResultList();
	}
}
