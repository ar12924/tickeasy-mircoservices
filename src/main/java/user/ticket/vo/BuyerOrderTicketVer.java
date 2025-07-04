package user.ticket.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import manager.eventdetail.vo.BuyerTicketEventVer;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer_order") 
public class BuyerOrderTicketVer {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "order_id")
	    private Long orderId;
	 	@Column(name="event_id")
	 	private Integer eventId;
	 	@Column(name = "member_id")
	    private Integer memberId;
	    @Column(name = "order_time")
	    private Timestamp orderTime;

	    @Column(name = "is_paid")
	    private Boolean isPaid;

	    @Column(name = "total_amount")
	    private BigDecimal totalAmount;
	    @Column(name="order_status")
	    private String orderStatus;
	    
	    @OneToOne
		@JoinColumn(name="event_id", insertable = false, updatable = false)
		private EventInfoTicketVer eventInfoTicketVer;

}
