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
@Table(name = "favorite")
public class Favorite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "favorite_id")
	private Integer favoriteId;
	@Column(name = "member_id")
	private Integer memberId;
	@Column(name = "event_id")
	private Integer eventId;
	@Column(name = "is_followed")
	private Integer isFollowed;
	@Column(name = "create_time", insertable = false, updatable = false)
	private Timestamp createTime;
	@Column(name = "update_time", insertable = false)
	private Timestamp updateTime;
}
