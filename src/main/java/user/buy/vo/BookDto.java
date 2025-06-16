package user.buy.vo;

import java.util.List;
import lombok.Data;

@Data
public class BookDto {
	/**
	 * 購票頁面暫存 vo (儲存使用者暫存訂購資訊)
	 */
	private Integer memberId;
	private Integer eventId;
	private String eventName;
	private Integer progress;
	private List<Selected> selected;
}
