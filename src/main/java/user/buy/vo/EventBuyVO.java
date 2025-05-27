package user.buy.vo;
/**
 * 活動資訊服務實現類
 * 創建者: archchang
 * 創建日期: 2025-05-13
 */
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "event_info")
public class EventBuyVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_from_date", nullable = false)
    private Date eventFromDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_to_date", nullable = false)
    private Date eventToDate;
    
    @Column(name = "event_host", nullable = false, length = 200)
    private String eventHost;
    
    @Column(name = "total_capacity")
    private Integer totalCapacity;
    
    @Column(name = "place", nullable = false, length = 200)
    private String place;
    
    @Column(name = "summary", length = 500)
    private String summary;
    
    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;
    
    @Column(name = "is_posted")
    private Integer posted;
    
    @Column(name = "image_dir", length = 255)
    private String imageDir;
    
    @Lob
    @Column(name = "image")
    private byte[] image;
    
    @Column(name = "member_id", nullable = false)
    private Integer memberId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", insertable = false, updatable = false)
    private Date createTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", insertable = false, updatable = false)
    private Date updateTime;
    
    @Transient
    private Integer remainingTickets;
    
    @Transient
    private Integer followed;
    
    // Getters and Setters
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

    public Date getEventFromDate() {
        return eventFromDate;
    }

    public void setEventFromDate(Date eventFromDate) {
        this.eventFromDate = eventFromDate;
    }

    public Date getEventToDate() {
        return eventToDate;
    }

    public void setEventToDate(Date eventToDate) {
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

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRemainingTickets() {
        return remainingTickets;
    }

    public void setRemainingTickets(Integer remainingTickets) {
        this.remainingTickets = remainingTickets;
    }

    public Integer getFollowed() {
        return followed;
    }

    public void setFollowed(Integer followed) {
        this.followed = followed;
    }
}