package user.ticket.vo;
/**
 * 換票留言實體類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import javax.persistence.*;
import java.sql.Timestamp;
@Entity
@Table(name = "swap_comment")
public class SwapCommentVO {
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
    
    @Column(name = "swapped_time")
    private Timestamp swappedTime;
    
    @Column(name = "post_id")
    private Integer postId;
    
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;
    
    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

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

	public Timestamp getSwappedTime() {
		return swappedTime;
	}

	public void setSwappedTime(Timestamp swappedTime) {
		this.swappedTime = swappedTime;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
}
