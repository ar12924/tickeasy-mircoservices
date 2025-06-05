package user.buy.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import user.buy.vo.TempOrder;

@Repository
public interface RedisBookDao extends CrudRepository<TempOrder, Integer> {
}
