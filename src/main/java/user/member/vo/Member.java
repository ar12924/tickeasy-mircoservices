package user.member.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBER")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID")
	private Integer memberId;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "NICK_NAME")
	private String nickName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "BIRTH_DATE")
	private Date birthDate;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "ROLE_LEVEL")
	private Integer roleLevel;

	@Column(name = "IS_ACTIVE")
	private Integer isActive;

	@Column(name = "UNICODE")
	private String unicode;

	@Column(name = "ID_CARD")
	private String idCard;

	@Column(name = "PASSWORD")
	private String password;

	@Lob
	@Column(name = "PHOTO", columnDefinition = "MEDIUMBLOB")
	private byte[] photo;

	@CreationTimestamp
	@Column(name = "CREATE_TIME", insertable = false)
	private Timestamp createTime;

	@UpdateTimestamp
	@Column(name = "UPDATE_TIME", insertable = false)
	private Timestamp updateTime;

	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
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
