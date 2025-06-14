package user.ticket.vo;
/**
 * 換票貼文實體類
 * 創建者: archchang
 * 創建日期: 2025-05-26
 */
import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "swap_post")
public class SwapPostVO {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;
    
    @Column(name = "post_member_id", nullable = false)
    private Integer postMemberId;
    
    @Column(name = "post_ticket_id", nullable = false, unique = true)
    private Integer postTicketId;
    
    @Column(name = "post_description", columnDefinition = "TEXT")
    private String postDescription;
    
    @Column(name = "event_id", nullable = false)
    private Integer eventId;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
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

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
}
