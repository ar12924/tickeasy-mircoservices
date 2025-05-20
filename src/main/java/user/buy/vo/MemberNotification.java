package user.buy.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="member_notification")
public class MemberNotification {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	@Column(name="member_notification_id")
	private Integer memberNotificationId;
	@Column(name="notification_id")
	private Integer notificationId;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="is_read")
	private Integer isRead;
	@Column(name="is_visible")
	private Integer isVisible;
	@Column(name="notification_status")
	private Integer notificationStatus;
	private String title;
	private String message;
	@Column(name="link_url")
	private String linkUrl;
	@Column(name="read_time")
	private Timestamp readTime;
	@Column(name="send_time")
	private Timestamp sendTime;
	@Column(name="create_time")
	private Timestamp createTime;
	@Column(name="update_time")
	private Timestamp updateTime;
}
