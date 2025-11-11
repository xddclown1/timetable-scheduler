package com.scheduler.scheduler;

import com.scheduler.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BacktrackingSchedulerTest {

    private BacktrackingScheduler scheduler;
    private List<Course> courses;
    private List<Professor> professors;
    private List<Room> rooms;
    private List<TimeSlot> timeSlots;

    @BeforeEach
    void setUp() {
        SchedulerConfig config = SchedulerConfig.builder()
            .timeoutMillis(10000)
            .seed(42)
            .build();

        scheduler = new BacktrackingScheduler(config);

        // Create time slots
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
                .build(),
            TimeSlot.builder()
                .slotIndex(3)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build()
        );

        // Create professors
        professors = List.of(
            Professor.builder()
                .id("P001")
                .name("Dr. Smith")
                .maxLoad(4)
                .build(),
            Professor.builder()
                .id("P002")
                .name("Dr. Jones")
                .maxLoad(4)
                .build()
        );

        // Create rooms
        rooms = List.of(
            Room.builder()
                .id("R001")
                .name("Lecture Hall A")
                .capacity(100)
                .addFeature("projector")
                .addFeature("whiteboard")
                .build(),
            Room.builder()
                .id("R002")
                .name("Lecture Hall B")
                .capacity(80)
                .addFeature("projector")
                .build()
        );

        // Create courses
        courses = List.of(
            Course.builder()
                .id("CS101")
                .name("Introduction to Programming")
                .duration(1)
                .expectedEnrollment(50)
                .professorId("P001")
                .addRequiredFeature("projector")
                .build(),
            Course.builder()
                .id("CS102")
                .name("Data Structures")
                .duration(1)
                .expectedEnrollment(40)
                .professorId("P002")
                .addRequiredFeature("projector")
                .build()
        );
    }

    @Test
    void shouldScheduleAllCoursesSuccessfully() {
        ScheduleResult result = scheduler.schedule(courses, professors, rooms, timeSlots);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSchedule().getScheduledCoursesCount());
        assertTrue(result.getUnscheduledCourses().isEmpty());
    }

    @Test
    void shouldHandleMultiSlotCourses() {
        Course multiSlotCourse = Course.builder()
            .id("CS103")
            .name("Advanced Programming")
            .duration(2)
            .expectedEnrollment(50)
            .professorId("P001")
            .addRequiredFeature("projector")
            .build();

        List<Course> coursesWithMultiSlot = List.of(multiSlotCourse);

        ScheduleResult result = scheduler.schedule(coursesWithMultiSlot, professors, rooms, timeSlots);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSchedule().getScheduledCoursesCount());
    }

    @Test
    void shouldHandleImpossibleScheduling() {
        // Create a scenario where scheduling is impossible
        List<TimeSlot> limitedSlots = List.of(timeSlots.get(0));
        
        List<Course> manyCourses = List.of(
            Course.builder()
                .id("CS101")
                .name("Course 1")
                .duration(1)
                .expectedEnrollment(50)
                .professorId("P001")
                .build(),
            Course.builder()
                .id("CS102")
                .name("Course 2")
                .duration(1)
                .expectedEnrollment(50)
                .professorId("P001")
                .build()
        );

        ScheduleResult result = scheduler.schedule(manyCourses, professors, rooms, limitedSlots);

        assertFalse(result.getUnscheduledCourses().isEmpty());
    }

    @Test
    void shouldRespectProfessorAvailability() {
        Professor unavailableProfessor = Professor.builder()
            .id("P001")
            .name("Dr. Smith")
            .maxLoad(4)
            .addUnavailableTimeSlot(timeSlots.get(0))
            .addUnavailableTimeSlot(timeSlots.get(1))
            .addUnavailableTimeSlot(timeSlots.get(2))
            .build();

        List<Professor> profsWithUnavailable = List.of(unavailableProfessor, professors.get(1));

        ScheduleResult result = scheduler.schedule(courses, profsWithUnavailable, rooms, timeSlots);

        // CS101 taught by P001 should be unscheduled
        assertTrue(result.getUnscheduledCourses().contains("CS101"));
    }

    @Test
    void shouldRespectRoomCapacity() {
        Room smallRoom = Room.builder()
            .id("R003")
            .name("Small Room")
            .capacity(10)
            .addFeature("projector")
            .build();

        List<Room> smallRooms = List.of(smallRoom);

        ScheduleResult result = scheduler.schedule(courses, professors, smallRooms, timeSlots);

        assertFalse(result.getUnscheduledCourses().isEmpty());
    }

    @Test
    void shouldRespectRoomFeatures() {
        Room roomWithoutProjector = Room.builder()
            .id("R003")
            .name("Basic Room")
            .capacity(100)
            .build();

        List<Room> basicRooms = List.of(roomWithoutProjector);

        ScheduleResult result = scheduler.schedule(courses, professors, basicRooms, timeSlots);

        assertFalse(result.getUnscheduledCourses().isEmpty());
    }

    @Test
    void shouldReturnExecutionTime() {
        ScheduleResult result = scheduler.schedule(courses, professors, rooms, timeSlots);

        assertTrue(result.getExecutionTimeMillis() >= 0);
    }
}
