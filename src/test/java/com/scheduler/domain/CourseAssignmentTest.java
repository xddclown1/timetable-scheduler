package com.scheduler.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseAssignmentTest {

    private Course course;
    private Room room;
    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        course = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        room = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .build();

        timeSlots = List.of(
            TimeSlot.builder()
                .slotIndex(1)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build(),
            TimeSlot.builder()
                .slotIndex(2)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build()
        );
    }

    @Test
    void shouldCreateValidCourseAssignment() {
        CourseAssignment assignment = new CourseAssignment(course, room, timeSlots);

        assertEquals(course, assignment.getCourse());
        assertEquals(room, assignment.getRoom());
        assertEquals(timeSlots, assignment.getTimeSlots());
    }

    @Test
    void shouldThrowExceptionForNullCourse() {
        assertThrows(NullPointerException.class, () ->
            new CourseAssignment(null, room, timeSlots)
        );
    }

    @Test
    void shouldThrowExceptionForNullRoom() {
        assertThrows(NullPointerException.class, () ->
            new CourseAssignment(course, null, timeSlots)
        );
    }

    @Test
    void shouldThrowExceptionForNullTimeSlots() {
        assertThrows(IllegalArgumentException.class, () ->
            new CourseAssignment(course, room, null)
        );
    }

    @Test
    void shouldThrowExceptionForEmptyTimeSlots() {
        assertThrows(IllegalArgumentException.class, () ->
            new CourseAssignment(course, room, List.of())
        );
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotsDoNotMatchCourseDuration() {
        List<TimeSlot> wrongSizeSlots = List.of(
            TimeSlot.builder()
                .slotIndex(1)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build()
        );

        assertThrows(IllegalArgumentException.class, () ->
            new CourseAssignment(course, room, wrongSizeSlots)
        );
    }

    @Test
    void shouldReturnUnmodifiableTimeSlots() {
        CourseAssignment assignment = new CourseAssignment(course, room, timeSlots);

        assertThrows(UnsupportedOperationException.class, () ->
            assignment.getTimeSlots().add(
                TimeSlot.builder()
                    .slotIndex(3)
                    .dayOfWeek(DayOfWeek.MONDAY)
                    .startTime(LocalTime.of(11, 0))
                    .endTime(LocalTime.of(12, 0))
                    .build()
            )
        );
    }

    @Test
    void shouldImplementEqualsCorrectly() {
        CourseAssignment assignment1 = new CourseAssignment(course, room, timeSlots);
        CourseAssignment assignment2 = new CourseAssignment(course, room, timeSlots);

        assertEquals(assignment1, assignment2);
        assertEquals(assignment1.hashCode(), assignment2.hashCode());
    }
}
