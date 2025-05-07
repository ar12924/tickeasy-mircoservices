package user.buy.vo;

import java.util.Date;

/**
 * 關鍵字分類實體類
 */
public class KeywordCategory {
    private Integer keywordId;       // 關鍵字分類唯一識別碼
    private String keywordName1;     // 關鍵字分類名稱1
    private String keywordName2;     // 關鍵字分類名稱2
    private String keywordName3;     // 關鍵字分類名稱3
    private Date createTime;         // 創建時間
    private Date updateTime;         // 更新時間

    // Getters and Setters
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