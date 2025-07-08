package user.ticket.vo;
/**
 * 活動實體類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "event_info")
public class EventInfoVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "event_from_date", nullable = false)
    private Timestamp  eventFromDate;
    
    @Column(name = "event_to_date", nullable = false)
    private Timestamp  eventToDate;
    
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
    private Timestamp  createTime;
    
    @Column(name = "update_time", nullable = false)
    private Timestamp  updateTime;

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Timestamp  getEventFromDate() {
		return eventFromDate;
	}

	public void setEventFromDate(Timestamp  eventFromDate) {
		this.eventFromDate = eventFromDate;
	}

	public Timestamp  getEventToDate() {
		return eventToDate;
	}

	public void setEventToDate(Timestamp  eventToDate) {
		this.eventToDate = eventToDate;
	}

	public String getEventHost() {
		return eventHost;
	}

	public void setEventHost(String eventHost) {
		this.eventHost = eventHost;
	}

	public Integer getTotalCapacity() {
		return totalCapacity;
	}

	public void setTotalCapacity(Integer totalCapacity) {
		this.totalCapacity = totalCapacity;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getPosted() {
        return posted;
    }

    public void setPosted(Integer posted) {
        this.posted = posted;
    }

	public String getImageDir() {
		return imageDir;
	}

	public void setImageDir(String imageDir) {
		this.imageDir = imageDir;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Integer getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(Integer keywordId) {
		this.keywordId = keywordId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Timestamp  getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp  createTime) {
		this.createTime = createTime;
	}

	public Timestamp  getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp  updateTime) {
		this.updateTime = updateTime;
	}
}
