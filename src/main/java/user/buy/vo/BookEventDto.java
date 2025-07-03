package user.buy.vo;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  頁面上購票人請求頁面資料(event_info 部分)。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEventDto {
	/**
	 *  活動 id。
	 */
	private int eventId;
	/**
	 *  活動名稱。
	 */
	private String eventName;
	/**
	 *  開始時間。
	 */
	private Date eventFromDate;
	/**
	 *  結束時間。
	 */
	private Date eventToDate;
	/**
	 *  主辦人。
	 */
	private String eventHost;
	/**
	 *  活動地點。
	 */
	private String place;
	/**
	 *  是否上架。
	 */
	private int isPosted;
	
	
	 // 如果需要 Timestamp，可以提供轉換方法
    public Timestamp getEventFromTimestamp() {
        return eventFromDate != null ? new Timestamp(eventFromDate.getTime()) : null;
    }
    
    public Timestamp getEventToTimestamp() {
        return eventToDate != null ? new Timestamp(eventToDate.getTime()) : null;
    }
}
