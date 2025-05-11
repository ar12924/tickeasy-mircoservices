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
@Table(name="buyer_ticket")
public class BuyerTicket {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column(name="ticket_id")
	private Integer ticketId;
	@Column(name="order_id")
	private Integer orderId;
	private String email;
	private String phone;
	private BigDecimal price;
	private Integer status;
	@Column(name="id_card")
	private String idCard;
	@Column(name="current_holder_member_id")
	private Integer currentHolderMemberId;
	@Column(name="is_used")
	private Integer isUsed;
	@Column(name="participant_name")
	private String participantName;
	@Column(name="event_name")
	private String eventName;
	@Column(name="type_id")
	private Integer typeId;
	@Column(name="queue_id")
	private Integer queueId;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="update_time")
	private Timestamp updateTime;
}
