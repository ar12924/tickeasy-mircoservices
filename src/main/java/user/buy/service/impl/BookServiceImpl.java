package user.buy.service.impl;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import user.buy.dao.BookDao;
import user.buy.service.BookService;
import user.buy.vo.TempOrder;
import user.buy.vo.TicketType;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao dao;
    @Autowired
    private RedisTemplate<String,Object> template;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<TicketType> findTicketType(Integer eventId) {
        return dao.selectById(eventId);
    }

    @Override
    public void cacheOrder(List<TempOrder> tmpOrderLst) {
        // 1. 訂單資訊快取到 Redis
        int orderId = 1;
        String key = "BookOrder:" + orderId;
        template.opsForValue().set(key, tmpOrderLst);
    }
}
