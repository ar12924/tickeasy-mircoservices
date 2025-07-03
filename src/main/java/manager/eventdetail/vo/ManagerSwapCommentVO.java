package manager.eventdetail.vo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 換票留言實體類
 * 創建者: archchang
 * 創建日期: 2025-06-23
 */
@Entity
@Table(name = "swap_comment")
public class ManagerSwapCommentVO implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;
    
    @Column(name = "comment_member_id", nullable = false)
    private Integer commentMemberId;
    
    @Column(name = "comment_ticket_id", nullable = false, unique = true)
    private Integer commentTicketId;
    
    @Column(name = "comment_description", columnDefinition = "TEXT")
    private String commentDescription;
    
    @Column(name = "swapped_status")
    private Integer swappedStatus;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "swapped_time")
    private Date swappedTime;
    
    @Column(name = "post_id")
    private Integer postId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable = false)
    private Date updateTime;
    
    // 非持久化屬性，用於存放關聯的貼文資訊
    @Transient
    private Integer postMemberId;
    
    @Transient
    private Integer postTicketId;
    
    @Transient
    private String postDescription;
    
    @Transient
    private Integer eventId;
    
    // 建構子
    public ManagerSwapCommentVO() {
    }
    
    // Getter 和 Setter 方法
    public Integer getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }
    
    public Integer getCommentMemberId() {
        return commentMemberId;
    }
    
    public void setCommentMemberId(Integer commentMemberId) {
        this.commentMemberId = commentMemberId;
    }
    
    public Integer getCommentTicketId() {
        return commentTicketId;
    }
    
    public void setCommentTicketId(Integer commentTicketId) {
        this.commentTicketId = commentTicketId;
    }
    
    public String getCommentDescription() {
        return commentDescription;
    }
    
    public void setCommentDescription(String commentDescription) {
        this.commentDescription = commentDescription;
    }
    
    public Integer getSwappedStatus() {
        return swappedStatus;
    }
    
    public void setSwappedStatus(Integer swappedStatus) {
        this.swappedStatus = swappedStatus;
    }
    
    public Date getSwappedTime() {
        return swappedTime;
    }
    
    public void setSwappedTime(Date swappedTime) {
        this.swappedTime = swappedTime;
    }
    
    public Integer getPostId() {
        return postId;
    }
    
    public void setPostId(Integer postId) {
        this.postId = postId;
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
    
    public Integer getPostMemberId() {
        return postMemberId;
    }
    
    public void setPostMemberId(Integer postMemberId) {
        this.postMemberId = postMemberId;
    }
    
    public Integer getPostTicketId() {
        return postTicketId;
    }
    
    public void setPostTicketId(Integer postTicketId) {
        this.postTicketId = postTicketId;
    }
    
    public String getPostDescription() {
        return postDescription;
    }
    
    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
}