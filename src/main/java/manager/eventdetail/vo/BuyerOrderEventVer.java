package manager.eventdetail.vo;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BUYER_ORDER")
public class BuyerOrderEventVer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Integer orderId;

    @Column(name = "ORDER_TIME")
    private Timestamp orderTime;

    @Column(name = "IS_PAID")
    private Boolean isPaid;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "MEMBER_ID")
    private Integer memberId;

    @OneToMany(mappedBy = "buyerOrder")
    private transient List<BuyerTicketEventVer> buyerTicketEventVer; // 關聯回 buyer_ticket
}
