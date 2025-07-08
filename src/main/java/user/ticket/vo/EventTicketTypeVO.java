package user.ticket.vo;
/**
 * 活動票種實體類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
@Entity
@Table(name = "event_ticket_type")
public class EventTicketTypeVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;
    
    @Column(name = "sell_from_time", nullable = false)
    private Timestamp  sellFromTime;
    
    @Column(name = "sell_to_time", nullable = false)
    private Timestamp  sellToTime;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp  createTime;
    
    @Column(name = "update_time", nullable = false)
    private Timestamp  updateTime;

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

	public Timestamp  getSellFromTime() {
		return sellFromTime;
	}

	public void setSellFromTime(Timestamp  sellFromTime) {
		this.sellFromTime = sellFromTime;
	}

	public Timestamp  getSellToTime() {
		return sellToTime;
	}

	public void setSellToTime(Timestamp  sellToTime) {
		this.sellToTime = sellToTime;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
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
