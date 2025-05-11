package user.buy.vo;

import java.sql.Timestamp;

public class MemberNotification {
	private Integer member_notification_id;
	private Integer notification_id;
	private Integer member_id;
	private Integer is_read;
	private Integer is_visible;
	private Integer notification_status;
	private String title;
	private String message;
	private String link_url;
	private Timestamp read_time;
	private Timestamp send_time;
	private Timestamp create_time;
	private Timestamp update_time;

	public Integer getMember_notification_id() {
		return member_notification_id;
	}

	public void setMember_notification_id(Integer member_notification_id) {
		this.member_notification_id = member_notification_id;
	}

	public Integer getNotification_id() {
		return notification_id;
	}

	public void setNotification_id(Integer notification_id) {
		this.notification_id = notification_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public Integer getIs_read() {
		return is_read;
	}

	public void setIs_read(Integer is_read) {
		this.is_read = is_read;
	}

	public Integer getIs_visible() {
		return is_visible;
	}

	public void setIs_visible(Integer is_visible) {
		this.is_visible = is_visible;
	}

	public Integer getNotification_status() {
		return notification_status;
	}

	public void setNotification_status(Integer notification_status) {
		this.notification_status = notification_status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink_url() {
		return link_url;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	public Timestamp getRead_time() {
		return read_time;
	}

	public void setRead_time(Timestamp read_time) {
		this.read_time = read_time;
	}

	public Timestamp getSend_time() {
		return send_time;
	}

	public void setSend_time(Timestamp send_time) {
		this.send_time = send_time;
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
