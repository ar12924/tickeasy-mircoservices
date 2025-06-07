package user.buy.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import user.buy.vo.TempBook;

@Repository
public interface RedisBookDao extends CrudRepository<TempBook, Integer> {
}
