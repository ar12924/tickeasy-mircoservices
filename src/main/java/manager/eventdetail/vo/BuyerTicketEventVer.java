package manager.eventdetail.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "BUYER_TICKET")
@Getter
@Setter
@ToString(exclude = {"buyerOrder", "eventTicketType"})
@NoArgsConstructor
@AllArgsConstructor
public class BuyerTicketEventVer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private Integer ticketId;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID", insertable = false, updatable = false)
    private BuyerOrderEventVer buyerOrder;

    @Column(name = "ORDER_ID")
    private Integer orderId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "ID_CARD")
    private String idCard;

    @Column(name = "CURRENT_HOLDER_MEMBER_ID")
    private Integer currentHolderMemberId;

    @Column(name = "IS_USED")
    private Integer isUsed;

    @Column(name = "PARTICIPANT_NAME")
    private String participantName;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @ManyToOne
    @JoinColumn(name = "TYPE_ID", insertable = false, updatable = false)
    private EventTicketType eventTicketType;

    @Column(name = "TYPE_ID")
    private Integer typeId;

    @Column(name = "QUEUE_ID")
    private Integer queueId;

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    @Column(name = "UPDATE_TIME")
    private Timestamp updateTime;

    @Transient
    private Integer ticketQuantity; // 票券數量（由 BuyerOrder 提供）

    @Transient
    private LocalDateTime orderTime; // 訂單時間（由 BuyerOrder 提供）

    @Transient
    private String qrCodeContent;

}
