package com.scheduler.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    private Schedule schedule;
    private Course course1;
    private Course course2;
    private Room room1;
    private Room room2;
    private TimeSlot slot1;
    private TimeSlot slot2;

    @BeforeEach
    void setUp() {
        schedule = new Schedule();

        course1 = Course.builder()
            .id("CS101")
            .name("Introduction to Programming")
            .duration(1)
            .expectedEnrollment(50)
            .professorId("P001")
            .build();

        course2 = Course.builder()
            .id("CS102")
            .name("Data Structures")
            .duration(1)
            .expectedEnrollment(40)
            .professorId("P002")
            .build();

        room1 = Room.builder()
            .id("R001")
            .name("Lecture Hall A")
            .capacity(100)
            .build();

        room2 = Room.builder()
            .id("R002")
            .name("Lecture Hall B")
            .capacity(80)
            .build();

        slot1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        slot2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build();
    }

    @Test
    void shouldStartEmpty() {
        assertTrue(schedule.isEmpty());
        assertEquals(0, schedule.getScheduledCoursesCount());
    }

    @Test
    void shouldAddAssignment() {
        CourseAssignment assignment = new CourseAssignment(course1, room1, List.of(slot1));
        schedule.addAssignment(assignment);

        assertFalse(schedule.isEmpty());
        assertEquals(1, schedule.getScheduledCoursesCount());
        assertTrue(schedule.getAssignment("CS101").isPresent());
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateCourse() {
        CourseAssignment assignment1 = new CourseAssignment(course1, room1, List.of(slot1));
        CourseAssignment assignment2 = new CourseAssignment(course1, room2, List.of(slot2));

        schedule.addAssignment(assignment1);

        assertThrows(IllegalStateException.class, () ->
            schedule.addAssignment(assignment2)
        );
    }

    @Test
    void shouldRemoveAssignment() {
        CourseAssignment assignment = new CourseAssignment(course1, room1, List.of(slot1));
        schedule.addAssignment(assignment);
        schedule.removeAssignment(assignment);

        assertTrue(schedule.isEmpty());
        assertFalse(schedule.getAssignment("CS101").isPresent());
    }

    @Test
    void shouldGetAssignmentsForProfessor() {
        Course course3 = Course.builder()
            .id("CS103")
            .name("Algorithms")
            .duration(1)
            .expectedEnrollment(30)
            .professorId("P001")
            .build();

        CourseAssignment assignment1 = new CourseAssignment(course1, room1, List.of(slot1));
        CourseAssignment assignment2 = new CourseAssignment(course3, room2, List.of(slot2));

        schedule.addAssignment(assignment1);
        schedule.addAssignment(assignment2);

        List<CourseAssignment> professorAssignments = 
            schedule.getAssignmentsForProfessor("P001");

        assertEquals(2, professorAssignments.size());
        assertTrue(professorAssignments.contains(assignment1));
        assertTrue(professorAssignments.contains(assignment2));
    }

    @Test
    void shouldGetAssignmentsForRoom() {
        CourseAssignment assignment1 = new CourseAssignment(course1, room1, List.of(slot1));
        CourseAssignment assignment2 = new CourseAssignment(course2, room1, List.of(slot2));

        schedule.addAssignment(assignment1);
        schedule.addAssignment(assignment2);

        List<CourseAssignment> roomAssignments = schedule.getAssignmentsForRoom("R001");

        assertEquals(2, roomAssignments.size());
        assertTrue(roomAssignments.contains(assignment1));
        assertTrue(roomAssignments.contains(assignment2));
    }

    @Test
    void shouldCheckProfessorAvailability() {
        CourseAssignment assignment = new CourseAssignment(course1, room1, List.of(slot1));
        schedule.addAssignment(assignment);

        assertFalse(schedule.isProfessorAvailableAt("P001", slot1));
        assertTrue(schedule.isProfessorAvailableAt("P001", slot2));
        assertTrue(schedule.isProfessorAvailableAt("P002", slot1));
    }

    @Test
    void shouldCheckRoomAvailability() {
        CourseAssignment assignment = new CourseAssignment(course1, room1, List.of(slot1));
        schedule.addAssignment(assignment);

        assertFalse(schedule.isRoomAvailableAt("R001", slot1));
        assertTrue(schedule.isRoomAvailableAt("R001", slot2));
        assertTrue(schedule.isRoomAvailableAt("R002", slot1));
    }

    @Test
    void shouldReturnUnmodifiableAssignmentsList() {
        CourseAssignment assignment = new CourseAssignment(course1, room1, List.of(slot1));
        schedule.addAssignment(assignment);

        assertThrows(UnsupportedOperationException.class, () ->
            schedule.getAssignments().add(
                new CourseAssignment(course2, room2, List.of(slot2))
            )
        );
    }
}
