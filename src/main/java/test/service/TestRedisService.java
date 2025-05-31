package test.service;

import test.vo.Student;

import java.util.List;

public interface TestRedisService {
    public Student saveStudent();

    public List<Student> save2Students();

    public  Student findStudentById(String id);

    public void deleteStudentById(String id);

    public Iterable<Student> findAllStudents();
}
