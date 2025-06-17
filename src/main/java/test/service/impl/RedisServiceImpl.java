package test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.dao.RedisDao;
import test.service.RedisService;
import test.vo.Student;

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisDao dao;

    // 儲存一位學生
    @Override
    public Student saveStudent() {
        Student student = new Student();
        student.setId("TIA20308");
        student.setName("Yiwei");
        student.setGender(Student.Gender.MALE);
        student.setGrade(1);
        return dao.save(student);
    }

    // 透過 id 查詢一位學生
    @Override
    public Student findStudentById(String id) {
        return dao.findById(id).get();
    }
}
