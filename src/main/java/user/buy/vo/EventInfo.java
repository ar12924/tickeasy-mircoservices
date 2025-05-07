package user.buy.vo;

import java.util.Date;

/**
 * 活動實體類
 */
public class EventInfo {
    private Integer eventId;          // 活動唯一識別碼
    private String eventName;         // 活動名稱
    private Date eventFromDate;       // 活動開始日期時間
    private Date eventToDate;         // 活動結束日期時間
    private String eventHost;         // 活動主辦方
    private Integer totalCapacity;    // 活動總容納人數
    private String place;             // 活動地點
    private String summary;           // 活動摘要
    private String detail;            // 活動詳細內容
    private Integer isPosted;         // 是否已發布，1表示已發布，0表示未發布
    private String imageDir;          // 活動圖片目錄路徑
    private byte[] image;             // 活動圖片
    private Integer keywordId;        // 關鍵字ID
    private Integer memberId;         // 創建活動的會員ID
    private Date createTime;          // 記錄創建時間
    private Date updateTime;          // 記錄更新時間

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

    public Integer getIsPosted() {
        return isPosted;
    }

    public void setIsPosted(Integer isPosted) {
        this.isPosted = isPosted;
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
}