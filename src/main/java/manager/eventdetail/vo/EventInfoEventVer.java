package manager.eventdetail.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "EVENT_INFO")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventInfoEventVer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Integer eventId;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "MEMBER_ID")
    private Integer memberId; // 活動方

    @Column(name = "EVENT_FROM_DATE")
    private Timestamp eventFromDate;

    @Column(name = "EVENT_TO_DATE")
    private Timestamp eventToDate;
    
    @Column(name = "IS_POSTED")
    private Boolean isPosted;

    @Column(name = "TOTAL_CAPACITY")
    private Integer capacity;
} 