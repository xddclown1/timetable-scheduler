package com.scheduler.io;

import com.scheduler.domain.Course;
import com.scheduler.domain.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CourseParserTest {

    @TempDir
    Path tempDir;

    private Map<String, TimeSlot> timeSlotMap;

    @BeforeEach
    void setUp() {
        timeSlotMap = new HashMap<>();
        timeSlotMap.put("1", TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build());
        timeSlotMap.put("2", TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build());
    }

    @Test
    void shouldParseValidCourses() throws IOException, ParseException {
        Path file = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,2,50,P001,projector;computers,1;2\n");
            writer.write("CS102,Data Structures,1,40,P002,projector,\n");
        }

        CourseParser parser = new CourseParser(timeSlotMap);
        List<Course> courses = parser.parse(file);

        assertEquals(2, courses.size());
        
        Course course1 = courses.get(0);
        assertEquals("CS101", course1.getId());
        assertEquals("Introduction to Programming", course1.getName());
        assertEquals(2, course1.getDuration());
        assertEquals(50, course1.getExpectedEnrollment());
        assertEquals("P001", course1.getProfessorId());
        assertTrue(course1.getRequiredFeatures().contains("projector"));
        assertTrue(course1.getRequiredFeatures().contains("computers"));
        assertEquals(2, course1.getPreferredTimeWindows().size());
    }

    @Test
    void shouldThrowExceptionForInvalidDuration() throws IOException {
        Path file = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,invalid,50,P001,projector,\n");
        }

        CourseParser parser = new CourseParser(timeSlotMap);
        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void shouldThrowExceptionForUnknownTimeSlot() throws IOException {
        Path file = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,2,50,P001,projector,999\n");
        }

        CourseParser parser = new CourseParser(timeSlotMap);
        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void shouldHandleEmptyFeatures() throws IOException, ParseException {
        Path file = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,2,50,P001,,\n");
        }

        CourseParser parser = new CourseParser(timeSlotMap);
        List<Course> courses = parser.parse(file);

        assertEquals(1, courses.size());
        assertTrue(courses.get(0).getRequiredFeatures().isEmpty());
    }
}
