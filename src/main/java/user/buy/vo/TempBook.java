package user.buy.vo;

import java.util.List;
import lombok.Data;

@Data
public class TempBook {
	/**
	 * 購票頁面暫存 vo (儲存使用者暫存訂購資訊)
	 */
	private int memberId;
	private int eventId;
	private String eventName;
	private List<TempSelection> selections;
	private TempContactInfo contactInfo;
	private List<TempFansInfos> fansInfos;
}
