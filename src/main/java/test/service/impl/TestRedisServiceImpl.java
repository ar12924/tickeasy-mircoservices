package test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.dao.TestRedisDao;
import test.service.TestRedisService;
import test.vo.Student;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestRedisServiceImpl implements TestRedisService {
    @Autowired
    TestRedisDao dao;

    // 儲存一位學生
    @Override
    public Student saveStudent() {
        Student stdt = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
        return dao.save(stdt);
    }

    // 儲存二位學生
    @Override
    public List<Student> save2Students() {
        Student engStudent = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
        Student medStudent = new Student("Med2015001", "Gareth Houston", Student.Gender.MALE, 2);
        List<Student> stdts = new ArrayList<>();
        stdts.add(dao.save(engStudent));
        stdts.add(dao.save(medStudent));
        return stdts;
    }

    // 透過 id 查詢一位學生
    @Override
    public Student findStudentById(String id) {
        return dao.findById("Eng2015001").get();
    }

    // 透過 id 刪除一位學生
    @Override
    public void deleteStudentById(String id) {
        dao.deleteById(id);
    }

    // 查詢所有學生
    @Override
    public Iterable<Student> findAllStudents() {
       return dao.findAll();
    }
}
