package manager.eventdetail.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "DIST_TICKET")
public class DistTicket {
		@Id
		@GeneratedValue(strategy= GenerationType.IDENTITY)
		@Column(name="dist_id")
		private Integer distId; // 分票ID
		@Column(name="received_member_id")
		private Integer receivedMemberId;// 收到分票Member ID
		@Column(name="ticket_id")
		private Integer ticketId;// 票券ID
		@Column(name="disted_time")
		private Timestamp distedTime;// 分票時間
		@Column(name="order_id")
		private Integer orderId; // 訂單ID
		@Column(name="create_time")
		private Timestamp createTime; // 創建時間
		@Column(name="update_time")
		private Timestamp updateTime; // 記錄更新時間
		@OneToOne
		@JoinColumn(name="order_id", insertable = false, updatable = false)
		private BuyerOrderDistVer buyerOrder;
	
}
