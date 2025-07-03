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
@Table(name = "keyword_category")
public class KeywordCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "keyword_id")
	private Integer keywordId;
	@Column(name = "keyword_name1")
	private String keywordName1;
	@Column(name = "keyword_name2")
	private String keywordName2;
	@Column(name = "keyword_name3")
	private String keywordName3;
	@Column(name = "create_time", insertable = false, updatable = false)
	private Timestamp createTime;
	@Column(name = "update_time", insertable = false, updatable = false)
	private Timestamp updateTime;
}
