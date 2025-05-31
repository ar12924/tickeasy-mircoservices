package test.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import test.vo.Student;

import java.util.List;

@Repository
public interface TestRedisDao extends CrudRepository<Student, String> {}