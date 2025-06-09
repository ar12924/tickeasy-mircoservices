package user.buy.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TempSelection {
	private int typeId;
	private int quantity;
	private String categoryName;
	private BigDecimal price;
}
