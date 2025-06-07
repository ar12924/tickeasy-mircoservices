package test.service;

import test.vo.Student;

import java.util.List;

public interface RedisService {

    public Student saveStudent();

    public  Student findStudentById(String id);
}
