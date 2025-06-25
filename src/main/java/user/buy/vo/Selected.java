package user.buy.vo;

import lombok.Data;

@Data
public class Selected {
	// 票種 id
	private Integer typeId;
	
	// 選擇票數
	private Integer quantity;
	
	// 票種名
	private String categoryName;
}
