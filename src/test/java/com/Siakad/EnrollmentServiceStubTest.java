package com.Siakad;

import com.Siakad.exception.CourseNotFoundException;
import com.Siakad.exception.StudentNotFoundException;
import com.Siakad.service.EnrollmentService;
import com.Siakad.service.GradeCalculator;

import com.Siakad.stub.CourseRepositoryStub;
import com.Siakad.stub.NotificationServiceStub;
import com.Siakad.stub.StudentRepositoryStub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Menguji EnrollmentService menggunakan STUB manual (Soal 1B).
 * Fokus pada metode: validateCreditLimit() dan dropCourse().
 */
class EnrollmentServiceStubTest {

    private EnrollmentService enrollmentService;

    // Deklarasi Stubs
    private StudentRepositoryStub studentRepoStub;
    private CourseRepositoryStub courseRepoStub;
    private NotificationServiceStub notificationStub;
    private GradeCalculator gradeCalculator; // Pakai GradeCalculator ASLI

    @BeforeEach
    void setUp() {
        // Inisialisasi semua stub
        studentRepoStub = new StudentRepositoryStub();
        courseRepoStub = new CourseRepositoryStub();
        notificationStub = new NotificationServiceStub();
        gradeCalculator = new GradeCalculator(); // Objek asli, bukan stub/mock

        // Inject stubs (dan 1 objek asli) ke service
        enrollmentService = new EnrollmentService(
                studentRepoStub,
                courseRepoStub,
                notificationStub,
                gradeCalculator
        );
    }

    // --- Pengujian metode validateCreditLimit() ---
    @Nested
    @DisplayName("Tests for validateCreditLimit() (using STUB)")
    class ValidateCreditLimitStubTests {

        @Test
        @DisplayName("Test Batas SKS Sukses (IPK Tinggi)")
        void testValidateCreditLimit_Success() {
            // Student "s123" dari stub punya IPK 3.5
            // GradeCalculator asli akan hitung maks 24 SKS
            boolean result = enrollmentService.validateCreditLimit("s123", 24);
            assertTrue(result, "Harusnya boleh ambil 24 SKS");
        }

        @Test
        @DisplayName("Test Batas SKS Gagal (IPK Rendah)")
        void testValidateCreditLimit_Failed() {
            // Student "s456" dari stub punya IPK 1.8
            // GradeCalculator asli akan hitung maks 15 SKS
            boolean result = enrollmentService.validateCreditLimit("s456", 18); // Coba minta 18 SKS
            assertFalse(result, "Harusnya TIDAK boleh ambil 18 SKS");
        }

        @Test
        @DisplayName("Test Batas SKS Gagal (Student Not Found)")
        void testValidateCreditLimit_StudentNotFound() {
            // Student "s999" akan return null dari stub
            // Pastikan melempar exception yang benar
            assertThrows(StudentNotFoundException.class, () -> {
                enrollmentService.validateCreditLimit("s999", 20);
            });
        }
    }

    // --- Pengujian metode dropCourse() ---
    @Nested
    @DisplayName("Tests for dropCourse() (using STUB)")
    class DropCourseStubTests {

        @Test
        @DisplayName("Test Drop Course Sukses")
        void testDropCourse_Success() {
            // Student "s123" ada di stub
            // Course "CS101" ada di stub (enrolled awal 50)
            assertDoesNotThrow(() -> { // Pastikan tidak ada error saat dijalankan
                enrollmentService.dropCourse("s123", "CS101");
            });

            // Verifikasi State (Cek nilai variabel di dalam stub)
            // 1. Cek apakah count di course stub berkurang (50 -> 49)
            assertEquals(49, courseRepoStub.lastUpdatedEnrolledCount,
                    "Jumlah mahasiswa enroll harusnya berkurang jadi 49");

            // 2. Cek apakah notifikasi (email) "terkirim" ke alamat & subjek yg benar
            assertEquals("budi.stub@test.com", notificationStub.lastEmailSentTo,
                    "Email harusnya 'terkirim' ke budi.stub@test.com");
            assertEquals("Course Drop Confirmation", notificationStub.lastSubject,
                    "Subjek email harusnya 'Course Drop Confirmation'");
        }

        @Test
        @DisplayName("Test Drop Course Gagal (Student Not Found)")
        void testDropCourse_StudentNotFound() {
            // Student "s999" tidak ada di stub
            assertThrows(StudentNotFoundException.class, () -> {
                enrollmentService.dropCourse("s999", "CS101");
            });

            // Verifikasi State (Pastikan TIDAK ada yang berubah)
            assertEquals(-1, courseRepoStub.lastUpdatedEnrolledCount, // Masih default -1
                    "Course count tidak boleh berubah");
            assertNull(notificationStub.lastEmailSentTo, // Masih default null
                    "Email tidak boleh terkirim");
        }

        @Test
        @DisplayName("Test Drop Course Gagal (Course Not Found)")
        void testDropCourse_CourseNotFound() {
            // Student "s123" ada, tapi Course "BADCODE" tidak ada di stub
            assertThrows(CourseNotFoundException.class, () -> {
                enrollmentService.dropCourse("s123", "BADCODE");
            });

            // Verifikasi State (Pastikan TIDAK ada yang berubah)
            assertEquals(-1, courseRepoStub.lastUpdatedEnrolledCount,
                    "Course count tidak boleh berubah");
            assertNull(notificationStub.lastEmailSentTo,
                    "Email tidak boleh terkirim");
        }
    }
}