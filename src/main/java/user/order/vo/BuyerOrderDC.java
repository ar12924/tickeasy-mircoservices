//package user.order.vo;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//import javax.persistence.Transient;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import manager.event.vo.MngEventInfo;
//
//@Entity(name = "BuyerOrderDC")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "buyer_order")
//public class BuyerOrderDC {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "order_id")
//	private Integer orderId;
//
//	@Column(name = "event_id")
//	private Integer eventId;
//
//	@Column(name = "member_id")
//	private Integer memberId;
//
//	@Column(name = "order_time", nullable = false)
//	private Timestamp orderTime;
//
//	@Column(name = "is_paid")
//	private Boolean isPaid;
//
//	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
//	private BigDecimal totalAmount;
//
//	@Column(name = "order_status", nullable = false, length = 50)
//	private String orderStatus;
//
//	@Column(name = "create_time", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
//	private Timestamp createTime;
//
//	@Column(name = "update_time", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
//	private Timestamp updateTime;
//
//	@Transient
//	private boolean successful;
//
//	@Transient
//	private String message;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "event_id", insertable = false, updatable = false)
//	private MngEventInfo mngEventInfo;
//}


package user.order.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer_order")
public class BuyerOrderDC {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    
    @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "member_id") 
    private Integer memberId;
    
    @Column(name = "order_time", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp orderTime;
    
    @Column(name = "is_paid")
    private Boolean isPaid;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "order_status", nullable = false, length = 50)
    private String orderStatus;
    
    @Column(name = "create_time", insertable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createTime;
    
    @Column(name = "update_time", insertable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp updateTime;
}