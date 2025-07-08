package user.buy.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "event_info")
public class EventInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private int eventId;
	@Column(name = "event_name")
	private String eventName;
	@Column(name = "event_from_date")
	private Timestamp eventFromDate;
	@Column(name = "event_to_date")
	private Timestamp eventToDate;
	@Column(name = "event_host")
	private String eventHost;
	@Column(name = "total_capacity")
	private int totalCapacity;
	private String place;
	private String summary;
	private String detail;
	@Column(name = "is_posted")
	private int isPosted;
	@Column(name = "image_dir")
	private String imageDir;
	@Lob
	@Column(name = "image")
	private byte[] image;
	@Column(name = "keyword_id")
	private int keywordId;
	@Column(name = "member_id")
	private int memberId;
	@Column(name = "create_time")
	private Timestamp createTime;
	@Column(name = "update_time")
	private Timestamp updateTime;
	
	

	public EventInfo createevent(EventInfo eventInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}
