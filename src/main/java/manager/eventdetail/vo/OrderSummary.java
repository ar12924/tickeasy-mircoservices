package manager.eventdetail.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummary {
    
    private Integer orderId;
    
    private Integer eventId;
    
    private String eventName;
    
    private Integer memberId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp orderTime;
    
    private Boolean isPaid;
    
    private BigDecimal totalAmount;
    
    private String orderStatus;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp updateTime;
}