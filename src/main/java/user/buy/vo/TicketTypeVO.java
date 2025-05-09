package user.buy.vo;

import java.io.Serializable;

public class TicketTypeVO implements Serializable{
	private static final long serialVersionUID = 1L;
    
    private Integer typeId;
    private String categoryName;
    private String sellFromTime;
    private String sellToTime;
    private Double price;
    private Integer capacity;
    private Integer eventId;
    private Integer remainingTickets;
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
	public String getSellFromTime() {
		return sellFromTime;
	}
	public void setSellFromTime(String sellFromTime) {
		this.sellFromTime = sellFromTime;
	}
	public String getSellToTime() {
		return sellToTime;
	}
	public void setSellToTime(String sellToTime) {
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
	public Integer getRemainingTickets() {
		return remainingTickets;
	}
	public void setRemainingTickets(Integer remainingTickets) {
		this.remainingTickets = remainingTickets;
	}
    
    
}
