package com.Siakad; // <- Pastikan S besar

// Import dari package main (S besar)
import com.Siakad.exception.*;
import com.Siakad.model.Course;
import com.Siakad.model.Enrollment;
import com.Siakad.model.Student;
import com.Siakad.repository.CourseRepository;
import com.Siakad.repository.StudentRepository;
import com.Siakad.service.EnrollmentService; // <- Pastikan service juga S besar
import com.Siakad.service.GradeCalculator;   // <- Pastikan service juga S besar
import com.Siakad.service.NotificationService; // <- Pastikan service juga S besar

// Import JUnit & Mockito
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Menguji EnrollmentService (khususnya metode enrollCourse) menggunakan MOCK (Mockito).
 * Ini untuk memenuhi Soal 1C.
 */
@ExtendWith(MockitoExtension.class) // Mengaktifkan Mockito
class EnrollmentServiceMockTest {

    // 1. Mocks untuk dependensi repository dan service lain
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private NotificationService notificationService;

    // --- TIDAK ADA @Mock untuk GradeCalculator ---
    // --- TIDAK ADA @InjectMocks ---

    // Deklarasi service yang akan diuji
    private EnrollmentService enrollmentService;
    // Deklarasi objek ASLI untuk GradeCalculator
    private GradeCalculator gradeCalculator;

    // Data dummy untuk digunakan di berbagai tes
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        // 1. objek GradeCalculator ASLI
        gradeCalculator = new GradeCalculator();

        // 2. Inisialisasi EnrollmentService secara manual
        //    dengan Mocks dan objek GradeCalculator asli
        enrollmentService = new EnrollmentService(
                studentRepository,
                courseRepository,
                notificationService,
                gradeCalculator // Inject objek asli
        );

        // 3. Setup data dummy yang "bersih" sebelum setiap tes
        student = new Student(); // Asumsi konstruktor default ada
        student.setStudentId("s123");
        student.setName("Budi Mock");
        student.setEmail("budi.mock@test.com");
        student.setAcademicStatus("ACTIVE"); // Status Awal
        student.setGpa(3.5);

        course = new Course(); // Asumsi konstruktor default ada
        course.setCourseCode("CS101");
        course.setCourseName("Dasar Pemrograman");
        course.setCapacity(50);
        course.setEnrolledCount(49); // Satu slot tersisa
    }

    @Test
    @DisplayName("[Enroll] Sukses mendaftarkan mahasiswa ke mata kuliah")
    void testEnrollCourse_Success() {
        // --- Arrange (Mengatur perilaku mock) ---
        when(studentRepository.findById("s123")).thenReturn(student);
        when(courseRepository.findByCourseCode("CS101")).thenReturn(course);
        when(courseRepository.isPrerequisiteMet("s123", "CS101")).thenReturn(true);

        // --- Act (Menjalankan metode yang diuji) ---
        Enrollment result = enrollmentService.enrollCourse("s123", "CS101");

        // --- Assert (Memeriksa hasil) ---
        assertNotNull(result, "Hasil enrollment tidak boleh null");
        assertEquals("APPROVED", result.getStatus(), "Status enrollment harus APPROVED");
        assertEquals("s123", result.getStudentId(), "Student ID di enrollment harus cocok");
        assertEquals("CS101", result.getCourseCode(), "Course Code di enrollment harus cocok");

        // Cek apakah enrolled count di objek course bertambah
        assertEquals(50, course.getEnrolledCount(), "Enrolled count harusnya bertambah jadi 50");

        // --- Verify (Memeriksa Interaksi/Behavior) ---
        // 1. Pastikan metode update() di courseRepo dipanggil TEPAT 1 KALI
        verify(courseRepository, times(1)).update(course);

        // 2. Pastikan metode sendEmail() dipanggil TEPAT 1 KALI dengan argumen yang benar
        verify(notificationService, times(1)).sendEmail(
                eq("budi.mock@test.com"), // Gunakan eq() untuk literal
                eq("Enrollment Confirmation"), // Gunakan eq() untuk literal
                contains("You have been enrolled in: Dasar Pemrograman") // Cek sebagian isi body email
        );
    }

    @Test
    @DisplayName("[Enroll] Gagal karena mahasiswa tidak ditemukan")
    void testEnrollCourse_Fail_StudentNotFound() {
        // Arrange: findById mengembalikan null
        when(studentRepository.findById("s999")).thenReturn(null);

        // Act & Assert: Pastikan melempar exception yang benar
        assertThrows(StudentNotFoundException.class, () -> {
            enrollmentService.enrollCourse("s999", "CS101");
        });

        // Verify: Pastikan tidak ada interaksi lebih lanjut (tidak perlu cek course, tidak kirim email)
        verify(courseRepository, never()).findByCourseCode(anyString());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("[Enroll] Gagal karena mahasiswa berstatus SUSPENDED")
    void testEnrollCourse_Fail_StudentSuspended() {
        // Arrange: Ubah status student jadi SUSPENDED
        student.setAcademicStatus("SUSPENDED");
        when(studentRepository.findById("s123")).thenReturn(student);

        // Act & Assert: Pastikan melempar exception EnrollmentException
        assertThrows(EnrollmentException.class, () -> {
            enrollmentService.enrollCourse("s123", "CS101");
        });

        // Verify: Tidak perlu cek course, tidak kirim email
        verify(courseRepository, never()).findByCourseCode(anyString());
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("[Enroll] Gagal karena mata kuliah tidak ditemukan")
    void testEnrollCourse_Fail_CourseNotFound() {
        // Arrange: Mahasiswa ditemukan, tapi findByCourseCode mengembalikan null
        when(studentRepository.findById("s123")).thenReturn(student);
        when(courseRepository.findByCourseCode("BADCODE")).thenReturn(null);

        // Act & Assert: Pastikan melempar CourseNotFoundException
        assertThrows(CourseNotFoundException.class, () -> {
            enrollmentService.enrollCourse("s123", "BADCODE");
        });

        // Verify: Tidak kirim email
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("[Enroll] Gagal karena mata kuliah sudah penuh")
    void testEnrollCourse_Fail_CourseFull() {
        // Arrange: Set course jadi penuh (enrolled == capacity)
        course.setEnrolledCount(50); // Kapasitas 50, sudah terisi 50
        when(studentRepository.findById("s123")).thenReturn(student);
        when(courseRepository.findByCourseCode("CS101")).thenReturn(course);

        // Act & Assert: Pastikan melempar CourseFullException
        assertThrows(CourseFullException.class, () -> {
            enrollmentService.enrollCourse("s123", "CS101");
        });

        // Verify: Tidak kirim email
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("[Enroll] Gagal karena prasyarat tidak terpenuhi")
    void testEnrollCourse_Fail_PrerequisiteNotMet() {
        // Arrange: Mahasiswa & Course ditemukan, tapi isPrerequisiteMet mengembalikan false
        when(studentRepository.findById("s123")).thenReturn(student);
        when(courseRepository.findByCourseCode("CS101")).thenReturn(course);
        when(courseRepository.isPrerequisiteMet("s123", "CS101")).thenReturn(false);

        // Act & Assert: Pastikan melempar PrerequisiteNotMetException
        assertThrows(PrerequisiteNotMetException.class, () -> {
            enrollmentService.enrollCourse("s123", "CS101");
        });

        // Verify: Pastikan email TIDAK terkirim dan course TIDAK di-update
        verify(notificationService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(courseRepository, never()).update(any(Course.class)); // Pastikan tidak ada pemanggilan update
    }
}