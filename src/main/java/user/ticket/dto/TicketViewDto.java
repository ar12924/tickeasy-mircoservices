package user.ticket.dto;

import java.sql.Timestamp;

public class TicketViewDto {
	private Integer ticketId;
	private Integer orderId;
	private String email;
	private String phone;
	private Double price;
	private Integer status;
	private String idCard;
	private Integer currentHolderMemberId;
	private Integer memberId;
	private Integer isUsed;
	private String participantName;
	private String eventName;
	private String categoryName;
	private Integer queueId;
	private Timestamp eventFromDate;
	private String place;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String isUsedText;
	private String statusText;
	private Integer viewCategoryType; // isUpcoming=1 isExpired=2 isTransferred=3;
	
	

	public Integer getViewCategoryType() {
		return viewCategoryType;
	}
	public void setViewCategoryType(Integer viewCategoryType) {
		this.viewCategoryType = viewCategoryType;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public Integer getCurrentHolderMemberId() {
		return currentHolderMemberId;
	}
	public void setCurrentHolderMemberId(Integer currentHolderMemberId) {
		this.currentHolderMemberId = currentHolderMemberId;
	}
	public Integer getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}
	public String getParticipantName() {
		return participantName;
	}
	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getQueueId() {
		return queueId;
	}
	public void setQueueId(Integer queueId) {
		this.queueId = queueId;
	}
	public Timestamp getEventFromDate() {
		return eventFromDate;
	}
	public void setEventFromDate(Timestamp eventFromDate) {
		this.eventFromDate = eventFromDate;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
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
	public String getIsUsedText() {
		return isUsedText;
	}
	public void setIsUsedText(String isUsedText) {
		this.isUsedText = isUsedText;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	

}
