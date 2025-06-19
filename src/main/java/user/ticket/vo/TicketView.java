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
@Table(name = "ticket_view") 
public class TicketView {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="ticket_id")
	private Integer ticketId;
	@Column(name="order_id")
	private Integer orderId;
	@Column(name="email")
	private String email;
	@Column(name="phone")
	private String phone;
	@Column(name="price")
	private Double price;
	@Column(name="status")
	private Integer status;
	@Column(name="id_card")
	private String idCard;
	@Column(name="current_holder_member_id")
	private Integer currentHolderMemberId;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="is_used")
	private Integer isUsed;
	@Column(name="participant_name")
	private String participantName;
	@Column(name="event_name")
	private String eventName;
	@Column(name="category_name")
	private String categoryName;
	@Column(name="queue_id")
	private Integer queueId;
	@Column(name="event_from_date")
	private Timestamp eventFromDate;
	@Column(name="place")
	private String place;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="update_time")
	private Timestamp updateTime;
	
	
/*

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
	
	*/
	
	
}
