package user.buy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDto {
	/**
	 * 活動 id。 (使用者新增或移除關注活動)
	 */
	private Integer eventId;

}
