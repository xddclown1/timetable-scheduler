package com.scheduler.constraints;

import com.scheduler.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintValidatorTest {

    private ConstraintValidator validator;
    private Course course;
    private Room room;
    private TimeSlot timeSlot;
    private Professor professor;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        validator = ConstraintValidator.withDefaultConstraints();

        course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(1)
            .expectedEnrollment(50)
            .professorId("P001")
            .addRequiredFeature("projector")
            .build();

        room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .addFeature("projector")
            .addFeature("whiteboard")
            .build();

        timeSlot = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        professor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .build();

        schedule = new Schedule();
    }

    @Test
    void shouldPassValidationForValidAssignment() {
        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, room, timeSlot, professor, schedule);

        assertTrue(result.isValid());
        assertTrue(result.getFailedResults().isEmpty());
    }

    @Test
    void shouldFailWhenRoomCapacityInsufficient() {
        Room smallRoom = Room.builder()
            .id("R002")
            .name("Small Room")
            .capacity(20)
            .addFeature("projector")
            .build();

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, smallRoom, timeSlot, professor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("cannot accommodate")));
    }

    @Test
    void shouldFailWhenRoomMissingRequiredFeatures() {
        Room roomWithoutProjector = Room.builder()
            .id("R003")
            .name("Basic Room")
            .capacity(100)
            .build();

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, roomWithoutProjector, timeSlot, professor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("missing required features")));
    }

    @Test
    void shouldFailWhenProfessorUnavailable() {
        Professor unavailableProfessor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .addUnavailableTimeSlot(timeSlot)
            .build();

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, room, timeSlot, unavailableProfessor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("unavailable")));
    }

    @Test
    void shouldFailWhenProfessorAlreadyScheduled() {
        // Schedule a course for the professor at this time
        Course otherCourse = Course.builder()
            .id("CS102")
            .name("Data Structures")
            .duration(1)
            .expectedEnrollment(40)
            .professorId("P001")
            .build();

        Room otherRoom = Room.builder()
            .id("R004")
            .name("Other Room")
            .capacity(100)
            .build();

        CourseAssignment existingAssignment = new CourseAssignment(
            otherCourse, otherRoom, List.of(timeSlot));
        schedule.addAssignment(existingAssignment);

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, room, timeSlot, professor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("already scheduled")));
    }

    @Test
    void shouldFailWhenRoomAlreadyOccupied() {
        // Schedule another course in this room at this time
        Course otherCourse = Course.builder()
            .id("CS102")
            .name("Data Structures")
            .duration(1)
            .expectedEnrollment(40)
            .professorId("P002")
            .build();

        Professor otherProfessor = Professor.builder()
            .id("P002")
            .name("Dr. Jones")
            .maxLoad(4)
            .build();

        CourseAssignment existingAssignment = new CourseAssignment(
            otherCourse, room, List.of(timeSlot));
        schedule.addAssignment(existingAssignment);

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validate(course, room, timeSlot, professor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("already occupied")));
    }

    @Test
    void shouldValidateMultiSlotAssignment() {
        Course multiSlotCourse = Course.builder()
            .id("CS103")
            .name("Advanced Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .addRequiredFeature("projector")
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build();

        List<TimeSlot> slots = List.of(timeSlot, slot2);

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validateMultiSlot(multiSlotCourse, room, slots, professor, schedule);

        assertTrue(result.isValid());
    }

    @Test
    void shouldFailMultiSlotWhenSlotsNotConsecutive() {
        Course multiSlotCourse = Course.builder()
            .id("CS103")
            .name("Advanced Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .addRequiredFeature("projector")
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(3)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(11, 0))
            .endTime(LocalTime.of(12, 0))
            .build();

        List<TimeSlot> nonConsecutiveSlots = List.of(timeSlot, slot2);

        ConstraintValidator.ConstraintValidationResult result = 
            validator.validateMultiSlot(multiSlotCourse, room, nonConsecutiveSlots, professor, schedule);

        assertFalse(result.isValid());
        assertTrue(result.getAllMessages().stream()
            .anyMatch(msg -> msg.contains("not consecutive")));
    }
}
