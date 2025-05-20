package user.buy.vo;

import java.util.Date;
import javax.persistence.*;
/**
 * 活動資訊服務實現類
 * 創建者: archchang
 * 創建日期: 2025-05-13
 */
@Entity
@Table(name = "event_ticket_type")
public class TicketTypeVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sell_from_time", nullable = false)
    private Date sellFromTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sell_to_time", nullable = false)
    private Date sellToTime;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Double price;
    
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", insertable = false, updatable = false)
    private Date createTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", insertable = false, updatable = false)
    private Date updateTime;
    
    @Transient
    private Integer remainingTickets;

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Date getSellFromTime() {
		return sellFromTime;
	}

	public void setSellFromTime(Date sellFromTime) {
		this.sellFromTime = sellFromTime;
	}

	public Date getSellToTime() {
		return sellToTime;
	}

	public void setSellToTime(Date sellToTime) {
		this.sellToTime = sellToTime;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getRemainingTickets() {
		return remainingTickets;
	}

	public void setRemainingTickets(Integer remainingTickets) {
		this.remainingTickets = remainingTickets;
	}
	
    
}
