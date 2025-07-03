package user.buy.vo;

import java.util.List;
import lombok.Data;

@Data
public class BookDto {
	// 活動 id
	private Integer eventId;
	
	// 購票人 userName
	private String userName;
	
	// 活動名
	private String eventName;
	
	// 購票流程進度
	private Integer progress;
	
	// 票種選擇結果
	private List<Selected> selected;
	
	// 聯絡人資訊
	private Contact contact;
	
	// 入場者資訊
	private List<Attendee> attendee;
}
