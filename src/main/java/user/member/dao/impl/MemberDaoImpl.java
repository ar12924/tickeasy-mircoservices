package user.member.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import static user.member.util.JdbcUtil.getConnection;


import user.member.dao.MemberDao;
import user.member.vo.Member;


public class MemberDaoImpl implements MemberDao {

	@Override
	public boolean insert(Member member) {
		String sql = """
	            INSERT INTO member (
	                user_name, password, email, phone, birth_date, gender,
	                role_level, is_active, unicode, id_card, create_time, update_time
	            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	        """;
	    try (Connection conn = getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, member.getUserName());
	            pstmt.setString(2, member.getPassword());
	            pstmt.setString(3, member.getEmail());
	            pstmt.setString(4, member.getPhone());
	            
	            pstmt.setDate(5, member.getBirthDate());
	            pstmt.setString(6, member.getGender());
	            pstmt.setInt(7, member.getRoleLevel() != null ? member.getRoleLevel() : 0);
	            pstmt.setInt(8, member.getIsActive() != null ? member.getIsActive() : 1);
	            pstmt.setString(9, member.getUnicode());
	            pstmt.setString(10, member.getIdCard());
	            pstmt.setTimestamp(11, member.getCreateTime());
	            pstmt.setTimestamp(12, member.getUpdateTime());

	            return pstmt.executeUpdate() > 0;

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	

	@Override
	public boolean update(Member member) {
        final StringBuilder sql = new StringBuilder()
                .append("UPDATE member SET ");
            final String password = member.getPassword();
            if (password != null && !password.isEmpty()) {
                sql.append("password = ?, ");
            }
            sql.append("email = ?, phone = ?, birth_date = ?, gender = ?, unicode = ?, update_time = CURRENT_TIMESTAMP WHERE user_name = ?");

            try (
                Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())
            ) {
            	if (password != null && !password.isEmpty()) {
                    pstmt.setString(1, member.getPassword());
                }
                pstmt.setString(1, member.getEmail());
                pstmt.setString(2, member.getPhone());
                pstmt.setDate(3, member.getBirthDate());
                pstmt.setString(4, member.getGender());
                pstmt.setString(5, member.getUnicode());
                pstmt.setString(6, member.getUserName());
                return pstmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
	    }

	@Override
	public Member findByUserName(String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Member findById(int memberId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(int memberId) {
        final String sql = "DELETE FROM member WHERE memberId = ?";
        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}

	@Override
	public List<Member> listAll() {
		final String sql = "SELECT * FROM member ORDER BY member_id";
		List<Member> resultList = new ArrayList<>();
	try (
		Connection conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery()
	) {
	}
	catch (Exception e) {
		 e.printStackTrace();
	}
		return listAll();
	}
}

