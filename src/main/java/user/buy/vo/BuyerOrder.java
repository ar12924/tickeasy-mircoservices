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

@Entity
@Data
@Table(name = "buyer_order")
public class BuyerOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;
	@Column(name = "event_id")
	private Integer eventId;
	@Column(name = "member_id")
	private Integer memberId;
	@Column(name = "order_time")
	private Timestamp orderTime;
	@Column(name = "is_paid")
	private Integer isPaid;
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	@Column(name = "order_status")
	private String orderStatus;
	@Column(name = "create_time", insertable = false, updatable = false)
	private Timestamp createTime;
	@Column(name = "update_time", insertable = false)
	private Timestamp updateTime;
}
