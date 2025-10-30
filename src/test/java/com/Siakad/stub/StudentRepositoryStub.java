package com.Siakad.stub;

// Import yang benar
import com.Siakad.model.Course;
import com.Siakad.model.Student;
import com.Siakad.repository.StudentRepository;
import java.util.Collections;
import java.util.List;

public class StudentRepositoryStub implements StudentRepository {

    @Override
    public Student findById(String studentId) {
        if ("s123".equals(studentId)) {
            Student student = new Student();
            student.setStudentId("s123");
            student.setName("Budi Stub");
            student.setEmail("budi.stub@test.com");
            student.setGpa(3.5);
            return student; // <-- PASTIKAN RETURN INI ADA
        }
        if ("s456".equals(studentId)) {
            Student student = new Student();
            student.setStudentId("s456");
            student.setName("Citra Stub");
            student.setEmail("citra.stub@test.com");
            student.setGpa(1.8);
            return student; // <-- PASTIKAN RETURN INI ADA
        }
        // Jika tidak masuk ke if di atas, kembalikan null
        return null; // <-- PASTIKAN RETURN INI ADA
    }

    @Override
    public List<Course> getCompletedCourses(String studentId) {
        System.out.println("STUB: getCompletedCourses dipanggil untuk studentId: " + studentId);
        return Collections.emptyList();
    }

    @Override
    public void update(Student student) {
        System.out.println("STUB: StudentRepository update() dipanggil untuk student: " + student.getStudentId());
    }
}