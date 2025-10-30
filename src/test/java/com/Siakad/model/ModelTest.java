package com.Siakad.model;

import com.Siakad.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ModelTest {

    @Test
    void testConstructorsAndGettersSetters() {
        // --- Test Student ---
        Student student1 = new Student();
        student1.setStudentId("s1");
        student1.setName("Test Name");
        student1.setEmail("test@email.com");
        student1.setMajor("Test Major");
        student1.setSemester(1);
        student1.setGpa(3.0);
        student1.setAcademicStatus("ACTIVE");

        assertEquals("s1", student1.getStudentId());
        assertEquals("Test Name", student1.getName());
        assertEquals("test@email.com", student1.getEmail());
        assertEquals("Test Major", student1.getMajor());
        assertEquals(1, student1.getSemester());
        assertEquals(3.0, student1.getGpa());
        assertEquals("ACTIVE", student1.getAcademicStatus());

        // Test constructor berparameter
        Student student2 = new Student("s2", "Name 2", "email2@test.com", "Major 2", 2, 2.5, "PROBATION");
        assertNotNull(student2);

        // --- Test Course ---
        Course course1 = new Course();
        course1.setCourseCode("C1");
        // ... set dan get semua field Course ...
        assertEquals("C1", course1.getCourseCode());

        // --- Test CourseGrade ---
        CourseGrade grade1 = new CourseGrade();
        grade1.setCourseCode("CG1");
        // ... set dan get semua field CourseGrade ...
        assertEquals("CG1", grade1.getCourseCode());

        // --- Test Enrollment ---
        Enrollment enroll1 = new Enrollment();
        enroll1.setEnrollmentId("E1");
        // ... set dan get semua field Enrollment ...
        assertEquals("E1", enroll1.getEnrollmentId());
    }
}