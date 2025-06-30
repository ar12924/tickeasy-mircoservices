package manager.eventdetail.vo;

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
@Table(name = "BUYER_ORDER")
public class BuyerOrderDistVer {
		@Id
		@GeneratedValue(strategy= GenerationType.IDENTITY)
		@Column(name="order_id")
		private Integer orderId; // 訂單ID
		@Column(name="member_id")
		private Integer memberId;// Member ID
		
	
}

