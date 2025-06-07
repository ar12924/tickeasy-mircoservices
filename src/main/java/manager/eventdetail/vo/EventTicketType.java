package manager.eventdetail.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "EVENT_TICKET_TYPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TYPE_ID")
    private Integer typeId;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Column(name = "SELL_FROM_TIME")
    private Timestamp sellFromTime;

    @Column(name = "SELL_TO_TIME")
    private Timestamp sellToTime;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "EVENT_ID")
    private Integer eventId;

    @Column(name = "PRICE")
    private BigDecimal price;

    @OneToMany(mappedBy = "eventTicketType")
    private List<BuyerTicketEventVer> buyerTicketEventVer; // 票券與報名資料的關聯

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    @Column(name = "UPDATE_TIME")
    private Timestamp updateTime;

}
