package user.buy.vo;

import lombok.Data;

@Data
public class Attendee {
	// 入場者帳號名稱
	private String userName;

	// 入場者身分證字號
	private String idCard;
	
	// 票種識別 id
	private Integer typeId;
	
}
