package test.java.com.scheduler.constraints;

import com.scheduler.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorAvailabilityConstraintTest {

    private ProfessorAvailabilityConstraint constraint;
    private Course course;
    private Room room;
    private TimeSlot timeSlot;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        constraint = new ProfessorAvailabilityConstraint();

        course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(1)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .build();

        timeSlot = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        schedule = new Schedule();
    }

    @Test
    void shouldPassWhenProfessorIsAvailable() {
        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .build();

        ValidationResult result = constraint.validate(course, room, timeSlot, professor, schedule);

        assertTrue(result.isValid());
    }

    @Test
    void shouldFailWhenProfessorIsUnavailable() {
        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .addUnavailableTimeSlot(timeSlot)
            .build();

        ValidationResult result = constraint.validate(course, room, timeSlot, professor, schedule);

        assertFalse(result.isValid());
        assertFalse(result.getMessages().isEmpty());
    }

    @Test
    void shouldFailWhenProfessorIsAlreadyScheduled() {
        Professor professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .build();

        // Schedule another course for this professor at the same time
        Course otherCourse = Course.builder()
            .id("CS102")
            .name("Data Structures")
            .duration(1)
            .expectedEnrollment(40)
            .professorId("P001")
            .build();

        Room otherRoom = Room.builder()
            .id("R002")
            .name("Other Room")
            .capacity(100)
            .build();

        CourseAssignment existingAssignment = new CourseAssignment(
            otherCourse, otherRoom, List.of(timeSlot));
        schedule.addAssignment(existingAssignment);

        ValidationResult result = constraint.validate(course, room, timeSlot, professor, schedule);

        assertFalse(result.isValid());
    }

    @Test
    void shouldBeHardConstraint() {
        assertTrue(constraint.isHardConstraint());
    }

    @Test
    void shouldHaveCorrectName() {
        assertEquals("Professor Availability", constraint.getName());
    }
}
