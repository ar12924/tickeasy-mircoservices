package manager.event.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVENT_INFO")
public class EventInfo2 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ENENT_ID")
	private int eventId;
	@Column(name = "EVENT_NAME")
	private String eventName;
	@Column(name = "EVENT_FROM_DATE")
	private Timestamp eventFromDate;
	@Column(name = "EVENT_TO_DATE")
	private Timestamp eventToDate;
	@Column(name = "EVENT_HOST")
	private String eventHost;
	@Column(name = "TOTAL_CAPACITY")
	private int totalCapacity;
	private String place;
	private String summary;
	private String detail;
	@Column(name = "IS_POSTED")
	private int isPosted;
	@Column(name = "IMAGE_DIR")
	private String imageDir;
	private String image;
	@Column(name = "KEYWORD_ID")
	private int keywordId;
	@Column(name = "MEMBER_ID")
	private int memberId;
	@Column(name = "CREATE_TIME")
	private Timestamp createTime;
	@Column(name = "UPDATE_TIME")
	private Timestamp updateTime;

}
