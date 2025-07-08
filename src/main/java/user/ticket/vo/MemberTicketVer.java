package user.ticket.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member") 
public class MemberTicketVer {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="member_id")
	private Integer currentHolderChangeMemberId;
	@Column(name="user_name")
	private String currentHolderChangeUserName;
	@Column(name="nick_name")
	private String currentHolderChangeNickName;
	@Column(name="email")
	private String currentHolderChangeEmail;
	@Column(name="phone")
	private String currentHolderChangePhone;
	@Column(name="id_card")
	private String currentHolderChangeIdCard;
	
	

}
