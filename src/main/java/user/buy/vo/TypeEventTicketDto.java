package user.buy.vo;

import lombok.Data;

@Data
public class TypeEventTicketDto {
	/**
	 * 購票頁面暫存 vo (儲存使用者暫存訂購資訊)
	 */
	private Integer typeId;
	private String categoryName;
	private Integer price;
	private Integer capacity;
	private Integer eventId;
	private String eventName;
	private Boolean isPosted;
}
