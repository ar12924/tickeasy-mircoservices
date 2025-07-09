package user.buy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemainedTicketCountDto {
	// 已售數量
	private Integer count;
	// 票種數量
	private Integer capacity;
	// 票種 id
	private Integer typeId;
	// 活動 id
	private Integer eventId;
}
