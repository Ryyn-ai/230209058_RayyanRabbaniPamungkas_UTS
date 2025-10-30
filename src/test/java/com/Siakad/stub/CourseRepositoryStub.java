package com.Siakad.stub;

// Import dari package main Anda
import com.Siakad.model.Course;
import com.Siakad.repository.CourseRepository;

public class CourseRepositoryStub implements CourseRepository {

    // Variabel ini untuk "State Verification"
    public int lastUpdatedEnrolledCount = -1;

    @Override
    public Course findByCourseCode(String courseCode) {
        if ("CS101".equals(courseCode)) {
            Course course = new Course();
            course.setCourseCode("CS101");
            course.setCourseName("Dasar Pemrograman");
            course.setEnrolledCount(50); // Awalnya 50
            return course;
        }
        return null;
    }

    @Override
    public void update(Course course) {
        this.lastUpdatedEnrolledCount = course.getEnrolledCount();
    }

    @Override
    public boolean isPrerequisiteMet(String studentId, String courseCode) {
        return true; // Asumsi true untuk stub ini
    }
}