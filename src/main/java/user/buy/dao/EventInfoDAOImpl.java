package user.buy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import user.buy.vo.EventVO;
import user.buy.vo.TicketTypeVO;
/**
 * 活動資訊數據訪問實現類
 */
public class EventInfoDAOImpl implements EventInfoDAO{
	@Override
    public EventVO getEventInfoById(Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        EventVO eventInfo = null;
        
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
                eventInfo = mapResultSetToEventVO(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return eventInfo;
    }
    
    @Override
    public List<EventVO> getRecommendedEvents(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EventVO> events = new ArrayList<>();
        
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
                EventVO eventInfo = mapResultSetToEventVO(rs);
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
    public List<EventVO> searchEventsByKeyword(String keyword, int offset, int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EventVO> events = new ArrayList<>();
        
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
                EventVO eventInfo = mapResultSetToEventVO(rs);
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
    public List<TicketTypeVO> getEventTicketTypesByEventId(Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TicketTypeVO> ticketTypes = new ArrayList<>();
        
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
                TicketTypeVO ticketType = new TicketTypeVO();
                ticketType.setTypeId(rs.getInt("type_id"));
                ticketType.setCategoryName(rs.getString("category_name"));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ticketType.setSellFromTime(sdf.format(rs.getTimestamp("sell_from_time")));
                ticketType.setSellToTime(sdf.format(rs.getTimestamp("sell_to_time")));
                
                ticketType.setPrice(rs.getDouble("price"));
                ticketType.setCapacity(rs.getInt("capacity"));
                ticketType.setEventId(rs.getInt("event_id"));
                ticketType.setRemainingTickets(rs.getInt("remaining_tickets"));
                
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
    public Integer checkFavoriteStatus(Integer memberId, Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer favoriteStatus = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            // 檢查是否已經存在關注記錄
            String sql = "SELECT is_followed FROM favorite WHERE member_id = ? AND event_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, eventId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                favoriteStatus = rs.getInt("is_followed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return favoriteStatus;
    }
    
    @Override
    public boolean insertFavorite(Integer memberId, Integer eventId, Integer isFollowed) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            String sql = "INSERT INTO favorite (member_id, event_id, is_followed, create_time, update_time) " +
                         "VALUES (?, ?, ?, NOW(), NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, eventId);
            pstmt.setInt(3, isFollowed);
            
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
        
        return success;
    }
    
    @Override
    public boolean updateFavorite(Integer memberId, Integer eventId, Integer isFollowed) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            String sql = "UPDATE favorite SET is_followed = ?, update_time = NOW() " +
                         "WHERE member_id = ? AND event_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, isFollowed);
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, eventId);
            
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, null);
        }
        
        return success;
    }
    
    @Override
    public byte[] getEventImage(Integer eventId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        byte[] imageData = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT image FROM event_info WHERE event_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eventId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                imageData = rs.getBytes("image");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeAll(conn, pstmt, rs);
        }
        
        return imageData;
    }
    
    /**
     * 將ResultSet映射到EventVO
     */
    private EventVO mapResultSetToEventVO(ResultSet rs) throws SQLException {
        EventVO eventVO = new EventVO();
        eventVO.setEventId(rs.getInt("event_id"));
        eventVO.setEventName(rs.getString("event_name"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        eventVO.setEventFromDate(sdf.format(rs.getTimestamp("event_from_date")));
        eventVO.setEventToDate(sdf.format(rs.getTimestamp("event_to_date")));
        
        eventVO.setEventHost(rs.getString("event_host"));
        eventVO.setTotalCapacity(rs.getInt("total_capacity"));
        eventVO.setPlace(rs.getString("place"));
        eventVO.setSummary(rs.getString("summary"));
        eventVO.setDetail(rs.getString("detail"));
        eventVO.setIsPosted(rs.getInt("is_posted"));
        eventVO.setImageDir(rs.getString("image_dir"));
        eventVO.setKeywordId(rs.getInt("keyword_id"));
        eventVO.setMemberId(rs.getInt("member_id"));
        
        // 設置關鍵字
        eventVO.setKeyword1(rs.getString("keyword_name1"));
        eventVO.setKeyword2(rs.getString("keyword_name2"));
        eventVO.setKeyword3(rs.getString("keyword_name3"));
        
        return eventVO;
    }
}
