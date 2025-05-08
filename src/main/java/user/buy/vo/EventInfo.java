package user.buy.vo;

import java.sql.Timestamp;

public class EventInfo {
	private int event_id;
	private String event_name;
	private Timestamp event_from_date;
	private Timestamp event_to_date;
	private String event_host;
	private int total_capacity;
	private String place;
	private String summary;
	private String detail;
	private int is_posted;
	private String image_dir;
	private Object image;
	private int keyword_id;
	private int member_id;
	private Timestamp create_time;
	private Timestamp update_time;

	public int getEvent_id() {
		return event_id;
	}

	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public Timestamp getEvent_from_date() {
		return event_from_date;
	}

	public void setEvent_from_date(Timestamp event_from_date) {
		this.event_from_date = event_from_date;
	}

	public Timestamp getEvent_to_date() {
		return event_to_date;
	}

	public void setEvent_to_date(Timestamp event_to_date) {
		this.event_to_date = event_to_date;
	}

	public String getEvent_host() {
		return event_host;
	}

	public void setEvent_host(String event_host) {
		this.event_host = event_host;
	}

	public int getTotal_capacity() {
		return total_capacity;
	}

	public void setTotal_capacity(int total_capacity) {
		this.total_capacity = total_capacity;
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

	public int getIs_posted() {
		return is_posted;
	}

	public void setIs_posted(int is_posted) {
		this.is_posted = is_posted;
	}

	public String getImage_dir() {
		return image_dir;
	}

	public void setImage_dir(String image_dir) {
		this.image_dir = image_dir;
	}

	public Object getImage() {
		return image;
	}

	public void setImage(Object image) {
		this.image = image;
	}

	public int getKeyword_id() {
		return keyword_id;
	}

	public void setKeyword_id(int keyword_id) {
		this.keyword_id = keyword_id;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public Timestamp getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Timestamp update_time) {
		this.update_time = update_time;
	}
}
