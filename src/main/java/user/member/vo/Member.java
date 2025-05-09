package user.member.vo;

import java.sql.Date;
import java.sql.Timestamp;

public class Member {
	private Integer memberId;
    private String userName;
    private String email;
    private String phone;
    private Date birthDate;
    private String gender;
    private Integer roleLevel;
    private Integer isActive;
    private String unicode;
    private String idCard;
    private String password;
    private Timestamp createTime;
    private Timestamp updateTime;
    
    private boolean successful;
    private String message;
    

	
	public Integer getMemberId() {
		return memberId;
	}



	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public Date getBirthDate() {
		return birthDate;
	}



	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}



	public String getGender() {
		return gender;
	}



	public void setGender(String gender) {
		this.gender = gender;
	}



	public Integer getRoleLevel() {
		return roleLevel;
	}



	public void setRoleLevel(Integer roleLevel) {
		this.roleLevel = roleLevel;
	}



	public Integer getIsActive() {
		return isActive;
	}



	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}



	public String getUnicode() {
		return unicode;
	}



	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}



	public String getIdCard() {
		return idCard;
	}



	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public Timestamp getCreateTime() {
		return createTime;
	}



	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}



	public Timestamp getUpdateTime() {
		return updateTime;
	}



	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}


	public boolean isSuccessful() {
	    return successful;
	}

	public void setSuccessful(boolean successful) {
	    this.successful = successful;
	}

	public String getMessage() {
	    return message;
	}

	public void setMessage(String message) {
	    this.message = message;
	}
	
   
	
	@Override
	public String toString() {
		return "Member [memberId=" + memberId + ", userName=" + userName + ", email=" + email + ", phone=" + phone
				+ ", birthDate=" + birthDate + ", gender=" + gender + ", roleLevel=" + roleLevel + ", isActive="
				+ isActive + ", unicode=" + unicode + ", idCard=" + idCard + ", password=" + password + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}

}
