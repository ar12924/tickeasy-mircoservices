package user.buy.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BuyerTicket {
	private Integer ticket_id;
	private Integer order_id;
	private String email;
	private String phone;
	private BigDecimal price;
	private Integer status;
	private String id_card;
	private Integer current_holder_member_id;
	private Integer is_used;
	private String participant_name;
	private String event_name;
	private Integer type_id;
	private Integer quene_id;
	private Timestamp create_time;
	private Timestamp update_time;

	public Integer getTicket_id() {
		return ticket_id;
	}

	public void setTicket_id(Integer ticket_id) {
		this.ticket_id = ticket_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getId_card() {
		return id_card;
	}

	public void setId_card(String id_card) {
		this.id_card = id_card;
	}

	public Integer getCurrent_holder_member_id() {
		return current_holder_member_id;
	}

	public void setCurrent_holder_member_id(Integer current_holder_member_id) {
		this.current_holder_member_id = current_holder_member_id;
	}

	public Integer getIs_used() {
		return is_used;
	}

	public void setIs_used(Integer is_used) {
		this.is_used = is_used;
	}

	public String getParticipant_name() {
		return participant_name;
	}

	public void setParticipant_name(String participant_name) {
		this.participant_name = participant_name;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public Integer getQuene_id() {
		return quene_id;
	}

	public void setQuene_id(Integer quene_id) {
		this.quene_id = quene_id;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public Timestamp getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Timestamp update_time) {
		this.update_time = update_time;
	}
}
