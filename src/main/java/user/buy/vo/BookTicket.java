package user.buy.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookTicket {
    private Integer count;
    private String categoryName;
    private BigDecimal price;
}
