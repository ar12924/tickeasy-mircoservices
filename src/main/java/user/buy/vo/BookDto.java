package user.buy.vo;

import java.util.List;
import lombok.Data;

@Data
public class BookDto {
	/**
	 * 購票頁 -> 填寫資料頁 -> 確認頁
	 */
	private Integer eventId;
	private String userName;
	private String eventName;
	private Integer progress;
	private List<Selected> selected;
	private Contact contact;
	private List<Attendee> attendee;
}
