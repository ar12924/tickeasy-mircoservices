package user.member.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import user.member.dao.MemberDao;
import user.member.vo.Member;

public class MemberDaoImpl implements MemberDao {
	private DataSource ds;

	public MemberDaoImpl() {
		try {
			// 取得 ds (所有 CRUD 共用)
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/tickeasy");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Member selectMemberById(Integer id) {
		String sql = "SELECT * FROM member WHERE member_id = ?";
		try ( // 1. 建立連線
				Connection conn = ds.getConnection();
				// 2. 創建預備 sql 敘述
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
			// 3. 設定欄位型別
			pstmt.setInt(1, id);
			// 4. 取得 rs 物件，並遍歷每筆資料
			try (ResultSet rs = pstmt.executeQuery()) {
				Member member = new Member();
				if (rs.next()) {
					// 5. 將資料放入 vo
					member.setMember_id(id);
					member.setUser_name(rs.getString("user_name"));
					member.setEmail(rs.getString("email"));
					member.setPhone(rs.getString("phone"));
					member.setBirth_date(rs.getDate("birth_date"));
					member.setGender(rs.getString("gender"));
					member.setRole_level(rs.getInt("role_level"));
					member.setIs_active(rs.getInt("is_active"));
					member.setUnicode(rs.getString("unicode"));
					member.setId_card(rs.getString("id_card"));
					member.setPassword(rs.getString("password"));
					member.setPhoto(rs.getObject("photo"));
					member.setCreate_time(rs.getTimestamp("create_time"));
					member.setUpdate_time(rs.getTimestamp("update_time"));
				}
				// 6. 回傳 vo
				return member;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
