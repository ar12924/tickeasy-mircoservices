package test.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import test.vo.Student;

@Repository
public interface RedisDao extends CrudRepository<Student, String> {}