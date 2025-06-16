package user.buy.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  購票頁上購票人請求頁面資料(event_ticket_type 部分)。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTypeDto {
	/**
	 *  typeId - 票種 id。
	 */
	private Integer typeId;
	/**
	 *  categoryName - 票種名稱。
	 */
	private String categoryName;
	/**
	 *  price - 票價。
	 */
	private BigDecimal price;
	/**
	 *  capacity - 該票種對應票數。
	 */
	private Integer capacity;
}
