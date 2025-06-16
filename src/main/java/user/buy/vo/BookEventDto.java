package user.buy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  購票頁上購票人請求頁面資料(event_info 部分)。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEventDto {
	/**
	 *  eventId - 活動 id。
	 */
	private Integer eventId;
	/**
	 *  eventName - 活動名稱。
	 */
	private String eventName;
	/**
	 *  isPosted - 是否上架。
	 */
	private Integer isPosted;
}
