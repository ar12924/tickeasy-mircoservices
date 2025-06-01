package user.buy.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.buy.dao.BookDao;
import user.buy.dao.RedisBookDao;
import user.buy.service.BookService;
import user.buy.vo.BookOrder;
import user.buy.vo.TicketType;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao dao;
    @Autowired
    private RedisBookDao redisDao;

    @Override
    public List<TicketType> findTicketType(Integer eventId) {
        return dao.selectById(eventId);
    }

    @Override
    public List<BookOrder> cacheOrder(List<BookOrder> bookOrderLst) {
        return bookOrderLst.stream().map(savedOrder -> {
            return redisDao.save(savedOrder);
        }).collect(Collectors.toList());
    }
}
