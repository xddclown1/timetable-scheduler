package com.scheduler.integration;

import com.scheduler.domain.*;
import com.scheduler.io.*;
import com.scheduler.scheduler.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EndToEndSchedulingTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldScheduleFromFilesToOutput() throws IOException, ParseException {
        // Create test data files
        createTestFiles();

        // Parse input files
        Path timeSlotsFile = tempDir.resolve("timeslots.csv");
        TimeSlotParser timeSlotParser = new TimeSlotParser();
        List<TimeSlot> timeSlots = timeSlotParser.parse(timeSlotsFile);
        Map<String, TimeSlot> timeSlotMap = timeSlotParser.getSlotIdMap();

        Path professorsFile = tempDir.resolve("professors.csv");
        ProfessorParser professorParser = new ProfessorParser(timeSlotMap);
        List<Professor> professors = professorParser.parse(professorsFile);

        Path roomsFile = tempDir.resolve("rooms.csv");
        RoomParser roomParser = new RoomParser(timeSlotMap);
        List<Room> rooms = roomParser.parse(roomsFile);

        Path coursesFile = tempDir.resolve("courses.csv");
        CourseParser courseParser = new CourseParser(timeSlotMap);
        List<Course> courses = courseParser.parse(coursesFile);

        // Schedule courses
        SchedulerConfig config = SchedulerConfig.builder()
            .timeoutMillis(10000)
            .seed(42)
            .build();

        BacktrackingScheduler scheduler = new BacktrackingScheduler(config);
        ScheduleResult result = scheduler.schedule(courses, professors, rooms, timeSlots);

        // Verify results
        assertTrue(result.isSuccess());
        assertEquals(3, result.getSchedule().getScheduledCoursesCount());
        assertTrue(result.getUnscheduledCourses().isEmpty());

        // Write output
        Path outputFile = tempDir.resolve("schedule.txt");
        ScheduleWriter writer = new ScheduleWriter();
        writer.writeToFile(result.getSchedule(), outputFile);

        // Verify output file exists
        assertTrue(outputFile.toFile().exists());
        assertTrue(outputFile.toFile().length() > 0);
    }

    @Test
    void shouldHandleComplexSchedulingScenario() throws IOException, ParseException {
        createComplexTestFiles();

        // Parse all files
        Path timeSlotsFile = tempDir.resolve("timeslots.csv");
        TimeSlotParser timeSlotParser = new TimeSlotParser();
        List<TimeSlot> timeSlots = timeSlotParser.parse(timeSlotsFile);
        Map<String, TimeSlot> timeSlotMap = timeSlotParser.getSlotIdMap();

        Path professorsFile = tempDir.resolve("professors.csv");
        ProfessorParser professorParser = new ProfessorParser(timeSlotMap);
        List<Professor> professors = professorParser.parse(professorsFile);

        Path roomsFile = tempDir.resolve("rooms.csv");
        RoomParser roomParser = new RoomParser(timeSlotMap);
        List<Room> rooms = roomParser.parse(roomsFile);

        Path coursesFile = tempDir.resolve("courses.csv");
        CourseParser courseParser = new CourseParser(timeSlotMap);
        List<Course> courses = courseParser.parse(coursesFile);

        // Schedule with constraints
        SchedulerConfig config = SchedulerConfig.builder()
            .timeoutMillis(30000)
            .seed(42)
            .build();

        BacktrackingScheduler scheduler = new BacktrackingScheduler(config);
        ScheduleResult result = scheduler.schedule(courses, professors, rooms, timeSlots);

        // Verify scheduling respects constraints
        assertNotNull(result.getSchedule());
        
        // Check no professor double-booking
        for (Professor professor : professors) {
            List<CourseAssignment> profAssignments = 
                result.getSchedule().getAssignmentsForProfessor(professor.getId());
            
            for (int i = 0; i < profAssignments.size(); i++) {
                for (int j = i + 1; j < profAssignments.size(); j++) {
                    CourseAssignment a1 = profAssignments.get(i);
                    CourseAssignment a2 = profAssignments.get(j);
                    
                    // Check no overlapping time slots
                    for (TimeSlot slot1 : a1.getTimeSlots()) {
                        for (TimeSlot slot2 : a2.getTimeSlots()) {
                            assertFalse(slot1.overlapsWith(slot2),
                                "Professor " + professor.getId() + " is double-booked");
                        }
                    }
                }
            }
        }

        // Check no room double-booking
        for (Room room : rooms) {
            List<CourseAssignment> roomAssignments = 
                result.getSchedule().getAssignmentsForRoom(room.getId());
            
            for (int i = 0; i < roomAssignments.size(); i++) {
                for (int j = i + 1; j < roomAssignments.size(); j++) {
                    CourseAssignment a1 = roomAssignments.get(i);
                    CourseAssignment a2 = roomAssignments.get(j);
                    
                    for (TimeSlot slot1 : a1.getTimeSlots()) {
                        for (TimeSlot slot2 : a2.getTimeSlots()) {
                            assertFalse(slot1.overlapsWith(slot2),
                                "Room " + room.getId() + " is double-booked");
                        }
                    }
                }
            }
        }
    }

    private void createTestFiles() throws IOException {
        // Create timeslots.csv
        Path timeSlotsFile = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(timeSlotsFile.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            writer.write("1,MONDAY,09:00,10:00\n");
            writer.write("2,MONDAY,10:00,11:00\n");
            writer.write("3,TUESDAY,09:00,10:00\n");
            writer.write("4,TUESDAY,10:00,11:00\n");
        }

        // Create professors.csv
        Path professorsFile = tempDir.resolve("professors.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(professorsFile.toFile()))) {
            writer.write("professorId,name,maxLoad,unavailableSlots\n");
            writer.write("P001,Dr. Smith,4,\n");
            writer.write("P002,Dr. Jones,4,\n");
        }

        // Create rooms.csv
        Path roomsFile = tempDir.resolve("rooms.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsFile.toFile()))) {
            writer.write("roomId,name,capacity,features,unavailableSlots\n");
            writer.write("R001,Lecture Hall A,100,projector;whiteboard,\n");
            writer.write("R002,Lecture Hall B,80,projector,\n");
        }

        // Create courses.csv
        Path coursesFile = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(coursesFile.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,1,50,P001,projector,\n");
            writer.write("CS102,Data Structures,1,40,P002,projector,\n");
            writer.write("CS103,Algorithms,1,35,P001,projector,\n");
        }
    }

    private void createComplexTestFiles() throws IOException {
        // Create more complex test scenario with constraints
        Path timeSlotsFile = tempDir.resolve("timeslots.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(timeSlotsFile.toFile()))) {
            writer.write("slotId,dayOfWeek,startTime,endTime\n");
            for (int i = 1; i <= 20; i++) {
                int day = ((i - 1) / 4) % 5;
                int hour = 9 + ((i - 1) % 4);
                String dayName = switch (day) {
                    case 0 -> "MONDAY";
                    case 1 -> "TUESDAY";
                    case 2 -> "WEDNESDAY";
                    case 3 -> "THURSDAY";
                    default -> "FRIDAY";
                };
                writer.write(String.format("%d,%s,%02d:00,%02d:00\n", 
                    i, dayName, hour, hour + 1));
            }
        }

        Path professorsFile = tempDir.resolve("professors.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(professorsFile.toFile()))) {
            writer.write("professorId,name,maxLoad,unavailableSlots\n");
            writer.write("P001,Dr. Smith,5,1;5\n");
            writer.write("P002,Dr. Jones,5,\n");
            writer.write("P003,Dr. Brown,5,10;15\n");
        }

        Path roomsFile = tempDir.resolve("rooms.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsFile.toFile()))) {
            writer.write("roomId,name,capacity,features,unavailableSlots\n");
            writer.write("R001,Lecture Hall A,100,projector;whiteboard,\n");
            writer.write("R002,Lecture Hall B,80,projector,\n");
            writer.write("R003,Lab Room,50,projector;computers,\n");
        }

        Path coursesFile = tempDir.resolve("courses.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(coursesFile.toFile()))) {
            writer.write("courseId,name,duration,expectedEnrollment,professorId,requiredFeatures,preferredSlots\n");
            writer.write("CS101,Introduction to Programming,2,50,P001,projector,2;3\n");
            writer.write("CS102,Data Structures,1,40,P002,projector,\n");
            writer.write("CS103,Algorithms,2,35,P001,projector,\n");
            writer.write("CS104,Database Systems,1,45,P003,projector,\n");
            writer.write("CS105,Programming Lab,1,30,P002,projector;computers,\n");
        }
    }
}
