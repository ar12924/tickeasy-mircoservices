package user.buy.vo;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "keyword_category")
public class KeywordCategoryVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Integer keywordId;
    
    @Column(name = "keyword_name1", length = 50)
    private String keywordName1;
    
    @Column(name = "keyword_name2", length = 50)
    private String keywordName2;
    
    @Column(name = "keyword_name3", length = 50)
    private String keywordName3;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", insertable = false, updatable = false)
    private Date createTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", insertable = false, updatable = false)
    private Date updateTime;

	public Integer getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(Integer keywordId) {
		this.keywordId = keywordId;
	}

	public String getKeywordName1() {
		return keywordName1;
	}

	public void setKeywordName1(String keywordName1) {
		this.keywordName1 = keywordName1;
	}

	public String getKeywordName2() {
		return keywordName2;
	}

	public void setKeywordName2(String keywordName2) {
		this.keywordName2 = keywordName2;
	}

	public String getKeywordName3() {
		return keywordName3;
	}

	public void setKeywordName3(String keywordName3) {
		this.keywordName3 = keywordName3;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
    
    
}
