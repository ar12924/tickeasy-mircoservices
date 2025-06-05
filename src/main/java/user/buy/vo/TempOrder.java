package user.buy.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TempOrder implements Serializable {
    @Id
    private Integer orderId;
    private Integer eventId;
    private Integer count;
    private String categoryName;
    private BigDecimal price;
}
