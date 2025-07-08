package user.ticket.vo;
/**
 * 購買票券實體類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
@Entity
@Table(name = "buyer_ticket")
public class BuyerTicketVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;
    
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "id_card", length = 10)
    private String idCard;
    
    @Column(name = "current_holder_member_id")
    private Integer currentHolderMemberId;
    
    @Column(name = "is_used")
    private Integer used;
    
    @Column(name = "participant_name", length = 100)
    private String participantName;
    
    @Column(name = "event_name", length = 100)
    private String eventName;
    
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "queue_id")
    private Integer queueId;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp  createTime;
    
    @Column(name = "update_time", nullable = false)
    private Timestamp  updateTime;

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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
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

	public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getQueueId() {
		return queueId;
	}

	public void setQueueId(Integer queueId) {
		this.queueId = queueId;
	}

	public Timestamp  getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp  createTime) {
		this.createTime = createTime;
	}

	public Timestamp  getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp  updateTime) {
		this.updateTime = updateTime;
	}
}
