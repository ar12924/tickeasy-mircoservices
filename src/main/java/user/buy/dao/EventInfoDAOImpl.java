package user.buy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 活動資訊數據訪問實現類
 */
public class EventInfoDAOImpl implements EventInfoDAO{
	@Override
    public Map<String, Object> getEventInfoById(Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Object> eventInfo = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT e.*, k.keyword_name1, k.keyword_name2, k.keyword_name3 " +
                         "FROM event_info e " +
                         "LEFT JOIN keyword_category k ON e.keyword_id = k.keyword_id " +
                         "WHERE e.event_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                eventInfo = mapResultSetToEventInfo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return eventInfo;
    }
	
	@Override
    public List<Map<String, Object>> getRecommendedEvents(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> events = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT e.*, k.keyword_name1, k.keyword_name2, k.keyword_name3 " +
                         "FROM event_info e " +
                         "LEFT JOIN keyword_category k ON e.keyword_id = k.keyword_id " +
                         "WHERE e.is_posted = 1 " +
                         "AND e.event_from_date > NOW() " +
                         "ORDER BY e.create_time DESC " +
                         "LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> eventInfo = mapResultSetToEventInfo(rs);
                events.add(eventInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return events;
    }

    @Override
    public List<Map<String, Object>> searchEventsByKeyword(String keyword, int offset, int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> events = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT e.*, k.keyword_name1, k.keyword_name2, k.keyword_name3 " +
                         "FROM event_info e " +
                         "LEFT JOIN keyword_category k ON e.keyword_id = k.keyword_id " +
                         "WHERE e.is_posted = 1 " +
                         "AND (e.event_name LIKE ? OR e.summary LIKE ? OR e.detail LIKE ? " +
                         "OR k.keyword_name1 LIKE ? OR k.keyword_name2 LIKE ? OR k.keyword_name3 LIKE ?) " +
                         "ORDER BY e.event_from_date ASC " +
                         "LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            pstmt.setString(6, searchPattern);
            pstmt.setInt(7, offset);
            pstmt.setInt(8, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> eventInfo = mapResultSetToEventInfo(rs);
                events.add(eventInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return events;
    }
    
    @Override
    public List<Map<String, Object>> getEventTicketTypesByEventId(Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> ticketTypes = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT ett.*, " +
                         "(ett.capacity - COALESCE(" +
                         "  (SELECT COUNT(*) FROM buyer_ticket bt " +
                         "   JOIN buyer_order bo ON bt.order_id = bo.order_id " +
                         "   WHERE bt.type_id = ett.type_id AND bo.is_paid = 1), 0" +
                         ")) AS remaining_tickets " +
                         "FROM event_ticket_type ett " +
                         "WHERE ett.event_id = ? " +
                         "ORDER BY ett.price ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> ticketType = new HashMap<>();
                ticketType.put("typeId", rs.getInt("type_id"));
                ticketType.put("categoryName", rs.getString("category_name"));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ticketType.put("sellFromTime", sdf.format(rs.getTimestamp("sell_from_time")));
                ticketType.put("sellToTime", sdf.format(rs.getTimestamp("sell_to_time")));
                
                ticketType.put("price", rs.getDouble("price"));
                ticketType.put("capacity", rs.getInt("capacity"));
                ticketType.put("eventId", rs.getInt("event_id"));
                ticketType.put("remainingTickets", rs.getInt("remaining_tickets"));
                
                ticketTypes.add(ticketType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return ticketTypes;
    }
    
    @Override
    public boolean updateTicketTypeRemainingQuantity(Integer typeId, Integer quantity) {
        // 這個方法實際上不需要實現，因為我們通過buyer_ticket表來追踪已售出票券
        // 剩餘票數是通過SQL查詢計算的
        return true;
    }
    
    @Override
    public boolean hasEnoughRemainingTickets(Integer typeId, Integer quantity) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean hasEnough = false;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT ett.capacity - COUNT(bt.ticket_id) as remaining " +
                         "FROM event_ticket_type ett " +
                         "LEFT JOIN buyer_ticket bt ON ett.type_id = bt.type_id " +
                         "LEFT JOIN buyer_order bo ON bt.order_id = bo.order_id AND bo.is_paid = 1 " +
                         "WHERE ett.type_id = ? " +
                         "GROUP BY ett.type_id, ett.capacity";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, typeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int remaining = rs.getInt("remaining");
                hasEnough = remaining >= quantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return hasEnough;
    }
    
    @Override
    public boolean setEventFavoriteStatus(Integer memberId, Integer eventId, Integer isFollowed) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 檢查是否已經存在關注記錄
            String checkSql = "SELECT favorite_id FROM favorite WHERE member_id = ? AND event_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, memberId);
            checkStmt.setInt(2, eventId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // 更新現有記錄
                String updateSql = "UPDATE favorite SET is_followed = ?, update_time = NOW() " +
                                   "WHERE member_id = ? AND event_id = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setInt(1, isFollowed);
                pstmt.setInt(2, memberId);
                pstmt.setInt(3, eventId);
            } else {
                // 新增記錄
                String insertSql = "INSERT INTO favorite (member_id, event_id, is_followed, create_time, update_time) " +
                                   "VALUES (?, ?, ?, NOW(), NOW())";
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, eventId);
                pstmt.setInt(3, isFollowed);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;
            
            rs.close();
            checkStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
        
        return success;
    }
    
    /**
     * 將ResultSet映射到Map
     */
    private Map<String, Object> mapResultSetToEventInfo(ResultSet rs) throws SQLException {
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventId", rs.getInt("event_id"));
        eventInfo.put("eventName", rs.getString("event_name"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        eventInfo.put("eventFromDate", sdf.format(rs.getTimestamp("event_from_date")));
        eventInfo.put("eventToDate", sdf.format(rs.getTimestamp("event_to_date")));
        
        eventInfo.put("eventHost", rs.getString("event_host"));
        eventInfo.put("totalCapacity", rs.getInt("total_capacity"));
        eventInfo.put("place", rs.getString("place"));
        eventInfo.put("summary", rs.getString("summary"));
        eventInfo.put("detail", rs.getString("detail"));
        eventInfo.put("isPosted", rs.getInt("is_posted"));
        eventInfo.put("imageDir", rs.getString("image_dir"));
        eventInfo.put("keywordId", rs.getInt("keyword_id"));
        eventInfo.put("memberId", rs.getInt("member_id"));
        
        // 設置關鍵字
        eventInfo.put("keyword1", rs.getString("keyword_name1"));
        eventInfo.put("keyword2", rs.getString("keyword_name2"));
        eventInfo.put("keyword3", rs.getString("keyword_name3"));
        
        return eventInfo;
    }
}
