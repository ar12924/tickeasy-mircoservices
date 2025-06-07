package user.buy.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "event_ticket_type")
public class EventTicketType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "type_id")
	private Integer typeId;
	@Column(name = "category_name")
	private String categoryName;
	@Column(name = "sell_from_time")
	private Timestamp sellFromTime;
	@Column(name = "sell_to_time")
	private Timestamp sellToTime;
	private BigDecimal price;
	private Integer capacity;
	@Column(name = "event_id")
	private Integer eventId;
	@Column(name = "create_time")
	private Timestamp createTime;
	@Column(name = "update_time")
	private Timestamp updateTime;
}
