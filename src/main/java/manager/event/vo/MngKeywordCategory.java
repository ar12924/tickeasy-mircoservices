package manager.event.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "keyword_category")
public class MngKeywordCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "keyword_id", insertable = false)
	private Integer keywordId;

	@Column(name = "keyword_name1", length = 50)
	private String keywordName1;

	@Column(name = "keyword_name2", length = 50)
	private String keywordName2;

	@Column(name = "keyword_name3", length = 50)
	private String keywordName3;

	@Column(name = "create_time", insertable = false, updatable = false)
	private Timestamp createTime;

	@Column(name = "update_time", insertable = false, updatable = false)
	private Timestamp updateTime;

}
