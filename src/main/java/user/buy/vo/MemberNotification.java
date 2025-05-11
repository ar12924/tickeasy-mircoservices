package user.buy.vo;

import java.sql.Timestamp;

public class MemberNotification {
	private Integer memberNotificationId;
	private Integer notificationId;
	private Integer memberId;
	private Integer isRead;
	private Integer isVisible;
	private Integer notificationStatus;
	private String title;
	private String message;
	private String linkUrl;
	private Timestamp readTime;
	private Timestamp sendTime;
	private Timestamp createTime;
	private Timestamp updateTime;

	public Integer getMemberNotificationId() {
		return memberNotificationId;
	}

	public void setMemberNotificationId(Integer memberNotificationId) {
		this.memberNotificationId = memberNotificationId;
	}

	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public Integer getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Integer isVisible) {
		this.isVisible = isVisible;
	}

	public Integer getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(Integer notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public Timestamp getReadTime() {
		return readTime;
	}

	public void setReadTime(Timestamp readTime) {
		this.readTime = readTime;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
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
}
