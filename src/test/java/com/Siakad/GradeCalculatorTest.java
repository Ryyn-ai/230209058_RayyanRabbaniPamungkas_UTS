package com.Siakad; // Pastikan S besar

// Import dari package main (S besar)
import com.Siakad.model.CourseGrade;
import com.Siakad.service.GradeCalculator; // Pastikan GradeCalculator ada di service

// Import JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// Import Java Util
import java.util.Collections;
import java.util.List;

// Import Assertions
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test untuk GradeCalculator (Soal 1A).
 * TIDAK PERLU MOCKITO.
 */
class GradeCalculatorTest {

    private GradeCalculator calculator;

    private CourseGrade gradeA; // 4.0, 3 SKS
    private CourseGrade gradeB; // 3.0, 2 SKS
    private CourseGrade gradeC; // 2.0, 1 SKS

    @BeforeEach
    void setUp() {
        calculator = new GradeCalculator();

        // objek asli
        gradeA = new CourseGrade();
        gradeA.setGradePoint(4.0);
        gradeA.setCredits(3);

        gradeB = new CourseGrade();
        gradeB.setGradePoint(3.0);
        gradeB.setCredits(2);

        gradeC = new CourseGrade();
        gradeC.setGradePoint(2.0);
        gradeC.setCredits(1);
    }

    // --- Pengujian metode calculateGPA ---
    @Nested
    @DisplayName("Tests for calculateGPA()")
    class CalculateGPATests {

        @Test
        @DisplayName("Test IPK sukses dengan beberapa mata kuliah")
        void testCalculateGPASuccess() {
            List<CourseGrade> grades = List.of(gradeA, gradeB, gradeC);
            double gpa = calculator.calculateGPA(grades);
            assertEquals(3.33, gpa);
        }

        @Test
        @DisplayName("Test IPK dengan pembulatan (8/3 = 2.67)")
        void testCalculateGPARounding() {
            List<CourseGrade> grades = List.of(gradeB, gradeC);
            double gpa = calculator.calculateGPA(grades);
            assertEquals(2.67, gpa);
        }

        @Test
        @DisplayName("Test IPK dengan list null")
        void testCalculateGPANullList() {
            assertEquals(0.0, calculator.calculateGPA(null));
        }

        @Test
        @DisplayName("Test IPK dengan list kosong")
        void testCalculateGPAEmptyList() {
            assertEquals(0.0, calculator.calculateGPA(Collections.emptyList()));
        }

        @Test
        @DisplayName("Test IPK dengan total SKS 0")
        void testCalculateGPAZeroCredits() {
            gradeA.setCredits(0);
            List<CourseGrade> grades = List.of(gradeA);
            assertEquals(0.0, calculator.calculateGPA(grades));
        }

        @Test
        @DisplayName("Test IPK gagal jika Grade Point > 4.0")
        void testCalculateGPAInvalidPointHigh() {
            gradeA.setGradePoint(4.1);
            List<CourseGrade> grades = List.of(gradeA);
            assertThrows(IllegalArgumentException.class, () -> calculator.calculateGPA(grades));
        }

        @Test
        @DisplayName("Test IPK gagal jika Grade Point < 0")
        void testCalculateGPAInvalidPointLow() {
            gradeB.setGradePoint(-0.5);
            List<CourseGrade> grades = List.of(gradeB);
            assertThrows(IllegalArgumentException.class, () -> calculator.calculateGPA(grades));
        }
    }

    // --- Pengujian metode determineAcademicStatus (YANG BARU DITAMBAHKAN) ---
    @Nested
    @DisplayName("Tests for determineAcademicStatus()")
    class DetermineStatusTests {

        // Skenario Gagal (Input Invalid)
        @Test
        @DisplayName("Test Status gagal jika GPA < 0")
        void testStatusInvalidGPALow() {
            assertThrows(IllegalArgumentException.class, () -> calculator.determineAcademicStatus(-0.1, 3));
        }

        @Test
        @DisplayName("Test Status gagal jika GPA > 4.0")
        void testStatusInvalidGPAHigh() {
            assertThrows(IllegalArgumentException.class, () -> calculator.determineAcademicStatus(4.1, 3));
        }

        @Test
        @DisplayName("Test Status gagal jika Semester < 1")
        void testStatusInvalidSemester() {
            assertThrows(IllegalArgumentException.class, () -> calculator.determineAcademicStatus(3.0, 0));
        }

        // Skenario Semester 1-2
        @Test
        @DisplayName("Test Status Semester 1-2 (ACTIVE & PROBATION)")
        void testStatusSemester1to2() {
            assertEquals("ACTIVE", calculator.determineAcademicStatus(2.0, 1), "Semester 1, IPK 2.0 -> ACTIVE");
            assertEquals("ACTIVE", calculator.determineAcademicStatus(3.5, 2), "Semester 2, IPK 3.5 -> ACTIVE");
            assertEquals("PROBATION", calculator.determineAcademicStatus(1.99, 2), "Semester 2, IPK 1.99 -> PROBATION");
            assertEquals("PROBATION", calculator.determineAcademicStatus(0.5, 1), "Semester 1, IPK 0.5 -> PROBATION");
        }

        // Skenario Semester 3-4
        @Test
        @DisplayName("Test Status Semester 3-4 (ACTIVE, PROBATION, SUSPENDED)")
        void testStatusSemester3to4() {
            assertEquals("ACTIVE", calculator.determineAcademicStatus(2.25, 3), "Semester 3, IPK 2.25 -> ACTIVE");
            assertEquals("ACTIVE", calculator.determineAcademicStatus(4.0, 4), "Semester 4, IPK 4.0 -> ACTIVE");
            assertEquals("PROBATION", calculator.determineAcademicStatus(2.24, 3), "Semester 3, IPK 2.24 -> PROBATION");
            assertEquals("PROBATION", calculator.determineAcademicStatus(2.0, 4), "Semester 4, IPK 2.0 -> PROBATION");
            assertEquals("SUSPENDED", calculator.determineAcademicStatus(1.99, 3), "Semester 3, IPK 1.99 -> SUSPENDED");
            assertEquals("SUSPENDED", calculator.determineAcademicStatus(1.0, 4), "Semester 4, IPK 1.0 -> SUSPENDED");
        }

        // Skenario Semester 5+
        @Test
        @DisplayName("Test Status Semester 5+ (ACTIVE, PROBATION, SUSPENDED)")
        void testStatusSemester5plus() {
            assertEquals("ACTIVE", calculator.determineAcademicStatus(2.5, 5), "Semester 5, IPK 2.5 -> ACTIVE");
            assertEquals("ACTIVE", calculator.determineAcademicStatus(3.0, 8), "Semester 8, IPK 3.0 -> ACTIVE");
            assertEquals("PROBATION", calculator.determineAcademicStatus(2.49, 6), "Semester 6, IPK 2.49 -> PROBATION");
            assertEquals("PROBATION", calculator.determineAcademicStatus(2.0, 7), "Semester 7, IPK 2.0 -> PROBATION");
            assertEquals("SUSPENDED", calculator.determineAcademicStatus(1.99, 5), "Semester 5, IPK 1.99 -> SUSPENDED");
            assertEquals("SUSPENDED", calculator.determineAcademicStatus(0.0, 8), "Semester 8, IPK 0.0 -> SUSPENDED");
        }
    }

    // --- Pengujian metode calculateMaxCredits ---
    @Nested
    @DisplayName("Tests for calculateMaxCredits()")
    class MaxCreditsTests {

        // Skenario Gagal (Input Invalid)
        @Test
        @DisplayName("Test SKS gagal jika GPA < 0")
        void testCreditsInvalidGPALow() {
            assertThrows(IllegalArgumentException.class, () -> calculator.calculateMaxCredits(-0.1));
        }

        @Test
        @DisplayName("Test SKS gagal jika GPA > 4.0")
        void testCreditsInvalidGPAHigh() {
            assertThrows(IllegalArgumentException.class, () -> calculator.calculateMaxCredits(4.1));
        }

        // Skenario Aturan Bisnis
        @Test
        @DisplayName("Test SKS untuk IPK >= 3.0 (24 SKS)")
        void testCreditsRange_3_to_4() {
            assertEquals(24, calculator.calculateMaxCredits(4.0));
            assertEquals(24, calculator.calculateMaxCredits(3.0));
        }

        @Test
        @DisplayName("Test SKS untuk IPK 2.5-2.99 (21 SKS)")
        void testCreditsRange_2_5_to_2_99() {
            assertEquals(21, calculator.calculateMaxCredits(2.99));
            assertEquals(21, calculator.calculateMaxCredits(2.5));
        }

        @Test
        @DisplayName("Test SKS untuk IPK 2.0-2.49 (18 SKS)")
        void testCreditsRange_2_0_to_2_49() {
            assertEquals(18, calculator.calculateMaxCredits(2.49));
            assertEquals(18, calculator.calculateMaxCredits(2.0));
        }

        @Test
        @DisplayName("Test SKS untuk IPK < 2.0 (15 SKS)")
        void testCreditsRange_below_2_0() {
            assertEquals(15, calculator.calculateMaxCredits(1.99));
            assertEquals(15, calculator.calculateMaxCredits(0.0));
        }
    }
}