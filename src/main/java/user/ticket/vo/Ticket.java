package user.ticket.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import manager.eventdetail.vo.BuyerOrderDistVer;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer_ticket") 
public class Ticket {
	
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
	@OneToOne
	@JoinColumn(name="order_id", insertable = false, updatable = false)
	private BuyerOrderTicketVer buyerOrderTicketVer;
	@OneToOne
	@JoinColumn(name="type_id", insertable = false, updatable = false)
	private EventTicketTypeTicketVer eventTicketTypeTicketVer;
	

}
