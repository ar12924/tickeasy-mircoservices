package manager.event.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "MngEventInfo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_info")
public class MngEventInfo{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Integer eventId;

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "event_from_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Timestamp eventFromDate;

	@Column(name = "event_to_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Timestamp eventToDate;

	@Column(name = "event_host")
	private String eventHost;

	@Column(name = "total_capacity")
	private Integer totalCapacity;

	private String place;

	private String summary;

	private String detail;

	@Column(name = "is_posted", insertable = false, updatable = false)
	private Integer isPosted;

	@Column(name = "image_dir")
	private String imageDir;

	@Lob
	@Column(name = "image")
	private byte[] image;

	@Column(name = "keyword_id", nullable = false)
	private Integer keywordId;

	@Column(name = "member_id", nullable = false)
	private Integer memberId;

	@Column(name = "create_time", insertable = false, updatable = false)
	private Timestamp createTime;

	@Column(name = "update_time", insertable = false, updatable = false)
	private Timestamp updateTime;

}
