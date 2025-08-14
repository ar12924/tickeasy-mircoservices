package user.member.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// 已遷移至 microservices.member.vo.Member，保留型別以相容舊介面（非 @Entity）
public class Member {

	private Integer memberId;

	private String userName;

	private String nickName;

	private String email;

	private String phone;

	private Date birthDate;

	private String gender;

	private Integer roleLevel;

	private Integer isActive;

	private String unicode;

	private String idCard;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private byte[] photo;

    private String photoKey;

    private Timestamp createTime;

    private Timestamp updateTime;
	
    private transient List<VerificationToken> tokens;

	@Transient
	private boolean successful;

	@Transient
	private String message;

	@Transient
	private String rePassword;

	@Transient
	private Boolean agree;

	@Transient
	private Boolean hostApply;

}
