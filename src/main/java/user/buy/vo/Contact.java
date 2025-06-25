package user.buy.vo;

import lombok.Data;

@Data
public class Contact {
	// 聯絡人 userName
	private String userName;
	
	// 聯絡人 email
    private String email;
    
    // 聯絡人暱稱
    private String nickName;
    
    // 聯絡人手機
    private String phone;
}
