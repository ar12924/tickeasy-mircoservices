package user.buy.vo;

import java.util.Date;

/**
 * 票券類型實體類
 */
public class EventTicketType {
    private Integer typeId;           // 票券類型ID
    private String categoryName;      // 票券類別名稱
    private Date sellFromTime;        // 售票開始時間
    private Date sellToTime;          // 售票結束時間
    private Double price;             // 票券價格
    private Integer capacity;         // 票券容量
    private Integer eventId;          // 關聯活動ID
    private Date createTime;          // 記錄創建時間
    private Date updateTime;          // 記錄更新時間

    // Getters and Setters
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Date getSellFromTime() {
        return sellFromTime;
    }

    public void setSellFromTime(Date sellFromTime) {
        this.sellFromTime = sellFromTime;
    }

    public Date getSellToTime() {
        return sellToTime;
    }

    public void setSellToTime(Date sellToTime) {
        this.sellToTime = sellToTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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