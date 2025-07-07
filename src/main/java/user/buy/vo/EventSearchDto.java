package user.buy.vo;

import common.vo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首頁查詢請求參數物件。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchDto {
	/**
	 * 查詢關鍵字
	 */
	private String keyword;
	/**
	 * 查詢頁數
	 */
	private Integer page;
	/**
	 * 查詢排序方法
	 */
	private Order order;
}
