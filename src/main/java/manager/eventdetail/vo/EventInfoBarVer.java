package manager.eventdetail.vo;

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
@Table(name = "EVENT_INFO")
public class EventInfoBarVer {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	 @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
	

}
