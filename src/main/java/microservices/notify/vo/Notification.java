package microservices.notify.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MEMBER_NOTIFICATION")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_notification_id")
    private Integer memberNotificationId;
    @Column(name = "notification_id")
    private Integer notificationId;
    @Column(name = "member_id")
    private Integer memberId;
    @Column(name = "is_read")
    private Integer isRead;
    @Column(name = "is_visible")
    private Integer isVisible;
    @Column(name = "notification_status")
    private Integer notificationStatus;
    @Column(name = "title")
    private String title;
    @Column(name = "message")
    private String message;
    @Column(name = "link_url")
    private String linkURL;
    @Column(name = "read_time")
    private Timestamp readTime;
    @Column(name = "send_time")
    private Timestamp sendTime;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "update_time")
    private Timestamp updateTime;
}


