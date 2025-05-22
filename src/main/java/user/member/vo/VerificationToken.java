package user.member.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VERIFICATION_TOKEN")
public class VerificationToken {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID")
	private Integer tokenId;
    
    // 驗證的連結使用
    @Column(name = "TOKEN_NAME", unique = true)
    private String tokenName;
    
    // 驗證用途：EMAIL_VERIFY、RESET_PASSWORD
    @Column(name = "TOKEN_TYPE")
    private String tokenType;
    
    @Column(name = "EXPIRED_TIME")
    private Timestamp expiredTime;
    
    @CreationTimestamp
    @Column(name = "CREATED_TIME")
    private Timestamp createdTime;
    
    // 多對一關聯，連到 Member
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID")
    private Member member;

    
}
