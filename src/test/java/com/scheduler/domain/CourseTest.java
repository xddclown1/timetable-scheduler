package test.java.com.scheduler.domain;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void shouldCreateValidCourse() {
        Course course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .addRequiredFeature("projector")
            .build();

        assertEquals("CS101", course.getId());
        assertEquals("Introduction to Programming", course.getName());
        assertEquals(2, course.getDuration());
        assertEquals(50, course.getExpectedEnrollment());
        assertEquals("P001", course.getProfessorId());
        assertTrue(course.getRequiredFeatures().contains("projector"));
    }

    @Test
    void shouldThrowExceptionForNullCourseId() {
        assertThrows(NullPointerException.class, () ->
            Course.builder()
                .name("Introduction to Programming")
                .duration(2)
                .expectedEnrollment(50)
                .professorId("P001")
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForZeroDuration() {
        assertThrows(IllegalArgumentException.class, () ->
            Course.builder()
                .id("CS101")
                .name("Introduction to Programming")
                .duration(0)
                .expectedEnrollment(50)
                .professorId("P001")
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForNegativeDuration() {
        assertThrows(IllegalArgumentException.class, () ->
            Course.builder()
                .id("CS101")
                .name("Introduction to Programming")
                .duration(-1)
                .expectedEnrollment(50)
                .professorId("P001")
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForZeroEnrollment() {
        assertThrows(IllegalArgumentException.class, () ->
            Course.builder()
                .id("CS101")
                .name("Introduction to Programming")
                .duration(2)
                .expectedEnrollment(0)
                .professorId("P001")
                .build()
        );
    }

    @Test
    void shouldThrowExceptionForNullProfessorId() {
        assertThrows(NullPointerException.class, () ->
            Course.builder()
                .id("CS101")
                .name("Introduction to Programming")
                .duration(2)
                .expectedEnrollment(50)
                .build()
        );
    }

    @Test
    void shouldHandleMultipleRequiredFeatures() {
        Course course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .requiredFeatures(Set.of("projector", "computers", "whiteboard"))
            .build();

        assertEquals(3, course.getRequiredFeatures().size());
        assertTrue(course.getRequiredFeatures().containsAll(
            Set.of("projector", "computers", "whiteboard")));
    }

    @Test
    void shouldHandlePreferredTimeWindows() {
        TimeSlot preferred1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot preferred2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.WEDNESDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        Course course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .preferredTimeWindows(Set.of(preferred1, preferred2))
            .build();

        assertTrue(course.hasPreferredTimeWindows());
        assertEquals(2, course.getPreferredTimeWindows().size());
    }

    @Test
    void shouldReturnFalseWhenNoPreferredTimeWindows() {
        Course course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        assertFalse(course.hasPreferredTimeWindows());
    }

    @Test
    void shouldReturnUnmodifiableRequiredFeatures() {
        Course course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        assertThrows(UnsupportedOperationException.class, () ->
            course.getRequiredFeatures().add("new-feature")
        );
    }

    @Test
    void shouldImplementEqualsBasedOnId() {
        Course course1 = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        Course course2 = Course.builder()
            .id("CS101")
            .name("Different Name")
            .duration(3)
            .expectedEnrollment(100)
            .professorId("P002")
            .build();

        assertEquals(course1, course2);
        assertEquals(course1.hashCode(), course2.hashCode());
    }
}
