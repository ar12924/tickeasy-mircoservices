package user.ticket.vo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_info") 
public class EventInfoTicketVer {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "event_from_date", nullable = false)
    private Timestamp eventFromDate;
    
    @Column(name = "event_to_date", nullable = false)
    private Timestamp eventToDate;
    
    @Column(name = "event_host", nullable = false, length = 200)
    private String eventHost;
    
    @Column(name = "total_capacity")
    private Integer totalCapacity;
    
    @Column(name = "place", nullable = false, length = 200)
    private String place;
    
    @Column(name = "summary", length = 500)
    private String summary;
    
    @Lob
    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;
    
    @Column(name = "is_posted")
    private Integer posted;
    
    @Column(name = "image_dir")
    private String imageDir;
    
    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;
    
    @Column(name = "keyword_id")
    private Integer keywordId;
    
    @Column(name = "member_id", nullable = false)
    private Integer memberId;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

}
