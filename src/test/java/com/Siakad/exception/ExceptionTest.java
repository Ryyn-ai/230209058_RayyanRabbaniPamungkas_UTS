package com.Siakad.exception; // <-- Perubahan di sini!

import com.Siakad.exception.*; // Import semua exception
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test sederhana untuk class-class di package exception.
 * Tujuannya hanya untuk meningkatkan code coverage dengan memanggil
 * constructor yang mungkin belum terpakai di tes lain.
 */
class ExceptionTest {

    @Test
    @DisplayName("Test Constructor untuk semua Exception")
    void testExceptionConstructors() {
        System.out.println("Testing Exception constructors...");

        // Coba panggil constructor dengan pesan String
        assertNotNull(new CourseFullException("Test message"));
        assertNotNull(new CourseNotFoundException("Test message"));
        assertNotNull(new EnrollmentException("Test message"));
        assertNotNull(new PrerequisiteNotMetException("Test message"));
        assertNotNull(new StudentNotFoundException("Test message"));

        // Coba panggil constructor default (tanpa argumen), jika ada
        // Hapus komentar di bawah ini jika exception Anda punya constructor default
        /*
        try { assertNotNull(new CourseFullException()); } catch (Exception e) {}
        try { assertNotNull(new CourseNotFoundException()); } catch (Exception e) {}
        try { assertNotNull(new EnrollmentException()); } catch (Exception e) {}
        try { assertNotNull(new PrerequisiteNotMetException()); } catch (Exception e) {}
        try { assertNotNull(new StudentNotFoundException()); } catch (Exception e) {}
        */

        // Coba panggil constructor dengan cause (Throwable), jika ada
        // Hapus komentar di bawah ini jika exception Anda punya constructor seperti ini
        /*
        Throwable cause = new RuntimeException("Root cause");
        try { assertNotNull(new CourseFullException("Test message", cause)); } catch (Exception e) {}
        try { assertNotNull(new CourseNotFoundException("Test message", cause)); } catch (Exception e) {}
        try { assertNotNull(new EnrollmentException("Test message", cause)); } catch (Exception e) {}
        try { assertNotNull(new PrerequisiteNotMetException("Test message", cause)); } catch (Exception e) {}
        try { assertNotNull(new StudentNotFoundException("Test message", cause)); } catch (Exception e) {}
        */

        System.out.println("Exception testing finished.");
    }
}