package user.buy.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BookOrder implements Serializable {
    @Id
    private Integer orderId;
    private Integer eventId;
    private Integer count;
    private String categoryName;
    private BigDecimal price;
}
