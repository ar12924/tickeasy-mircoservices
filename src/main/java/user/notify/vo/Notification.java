package user.notify.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MEMBER_NOTIFICATION")
public class Notification {
		@Id
		@GeneratedValue(strategy= GenerationType.IDENTITY)
		@Column(name="member_notification_id")
		private Integer memberNotificationId; // 用戶通知ID
		@Column(name="notification_id")
		private Integer notificationId;// 通知ID
		@Column(name="member_id")
		private Integer memberId;// 用戶ID
		@Column(name="is_read")
		private Integer isRead;// 是否已讀，0:未讀，1:已讀
		@Column(name="is_visible")
		private Integer isVisible; // 是否可見，0:不可見，1:可見
		@Column(name="notification_status")
		private Integer notificationStatus; // 會員通知狀態，0:未發送，1:已發送
		@Column(name="title")
		private String title; // 通知標題
		@Column(name="message")
		private String message; // 通知內容
		@Column(name="link_url")
		private String linkURL; // 相關連結
		@Column(name="read_time")
		private Timestamp readTime;  // 閱讀時間
		@Column(name="send_time")
		private Timestamp sendTime; // 發送時間
		@Column(name="create_time")
		private Timestamp createTime; // 創建時間
		@Column(name="update_time")
		private Timestamp updateTime; // 記錄更新時間
		   /* 
		    `member_notification_id` INT NOT NULL AUTO_INCREMENT COMMENT '用戶通知ID',
		    `notification_id` INT NOT NULL COMMENT '通知ID',
		    `member_id` INT NOT NULL COMMENT '用戶ID',
		    `is_read` TINYINT(1) NOT NULL COMMENT '是否已讀，0:未讀，1:已讀',
		    `is_visible` TINYINT(1) NOT NULL COMMENT '是否可見，0:不可見，1:可見',
		    `notification_status` TINYINT(1) NOT NULL COMMENT '會員通知狀態，0:未發送，1:已發送',
		    `title` VARCHAR(255) NOT NULL COMMENT '通知標題',
		    `message` TEXT NOT NULL COMMENT '通知內容',
		    `link_url` VARCHAR(255) NULL COMMENT '相關連結',
		    `read_time` DATETIME NULL COMMENT '閱讀時間',
		    `send_time` DATETIME NULL COMMENT '發送時間',
		    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '創建時間',
		    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '記錄更新時間'
			*/
		
			/*
			 * public Integer getMemberNotificationId() { return memberNotificationId; }
			 * public void setMemberNotificationId(Integer memberNotification) {
			 * this.memberNotificationId = memberNotification; } public Integer
			 * getNotificationId() { return notificationId; } public void
			 * setNotificationId(Integer notificationId) { this.notificationId =
			 * notificationId; } public Integer getMemberId() { return memberId; } public
			 * void setMemberId(Integer memberId) { this.memberId = memberId; } public
			 * Integer getIsRead() { return isRead; } public void setIsRead(Integer isRead)
			 * { this.isRead = isRead; } public Integer getIsVisible() { return isVisible; }
			 * public void setIsVisible(Integer isVisible) { this.isVisible = isVisible; }
			 * public Integer getNotificationStatus() { return notificationStatus; } public
			 * void setNotificationStatus(Integer notificationStatus) {
			 * this.notificationStatus = notificationStatus; } public String getTitle() {
			 * return title; } public void setTitle(String title) { this.title = title; }
			 * public String getMessage() { return message; } public void setMessage(String
			 * message) { this.message = message; } public String getLinkURL() { return
			 * linkURL; } public void setLinkURL(String linkURL) { this.linkURL = linkURL; }
			 * public Timestamp getReadTime() { return readTime; } public void
			 * setReadTime(Timestamp readTime) { this.readTime = readTime; } public
			 * Timestamp getSendTime() { return sendTime; } public void
			 * setSendTime(Timestamp sendTime) { this.sendTime = sendTime; } public
			 * Timestamp getCreateTime() { return createTime; } public void
			 * setCreateTime(Timestamp createTime) { this.createTime = createTime; } public
			 * Timestamp getUpdateTime() { return updateTime; } public void
			 * setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
			 */
		
}
