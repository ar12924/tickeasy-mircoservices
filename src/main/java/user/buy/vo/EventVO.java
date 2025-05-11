package user.buy.vo;

import java.io.Serializable;
import java.util.Date;

public class EventVO implements Serializable{
	private static final long serialVersionUID = 1L;
    private Integer eventId;
    private String eventName;
    private String eventFromDate;
    private String eventToDate;
    private String eventHost;
    private Integer totalCapacity;
    private String place;
    private String summary;
    private String detail;
    private Integer isPosted;
    private String imageDir;
    private Integer keywordId;
    private Integer memberId;
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private Integer remainingTickets;
    private Integer isFollowed;
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
	public String getEventFromDate() {
		return eventFromDate;
	}
	public void setEventFromDate(String eventFromDate) {
		this.eventFromDate = eventFromDate;
	}
	public String getEventToDate() {
		return eventToDate;
	}
	public void setEventToDate(String eventToDate) {
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
	public String getKeyword1() {
		return keyword1;
	}
	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}
	public String getKeyword2() {
		return keyword2;
	}
	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}
	public String getKeyword3() {
		return keyword3;
	}
	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}
	public Integer getRemainingTickets() {
		return remainingTickets;
	}
	public void setRemainingTickets(Integer remainingTickets) {
		this.remainingTickets = remainingTickets;
	}
	public Integer getIsFollowed() {
		return isFollowed;
	}
	public void setIsFollowed(Integer isFollowed) {
		this.isFollowed = isFollowed;
	}
    
    
}
