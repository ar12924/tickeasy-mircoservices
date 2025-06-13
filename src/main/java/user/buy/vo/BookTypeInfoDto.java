package user.buy.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTypeInfoDto {
	private Integer typeId;
	private String categoryName;
	private BigDecimal price;
	private Integer capacity;
	private Integer eventId;
	private String eventName;
	private Integer isPosted;
}
