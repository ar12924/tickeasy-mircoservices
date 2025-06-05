package user.buy.vo;

import java.util.List;
import lombok.Data;

@Data
public class TempOrder {
	/**
	 * 購票頁面下一步暫存 vo
	 */
	private int MemberId;
	private int eventId;
	private String eventName;
	private List<TempSelection> selections;
}
