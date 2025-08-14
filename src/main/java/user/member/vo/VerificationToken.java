package user.member.vo;

import java.sql.Timestamp;

 

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// 已遷移至 microservices.member.vo.VerificationToken，保留型別以相容舊介面（非 @Entity）
public class VerificationToken {
	
	private Integer tokenId;
    
    // 驗證的連結使用
    private String tokenName;
    
    // 驗證用途：EMAIL_VERIFY、RESET_PASSWORD
    private String tokenType;
    
    private Timestamp expiredTime;
    
    private Timestamp createdTime;
    
    // 多對一關聯，連到 Member
    private Member member;

    
}
