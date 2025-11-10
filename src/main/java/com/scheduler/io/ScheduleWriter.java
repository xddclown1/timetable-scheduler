package com.scheduler.io;

import com.scheduler.domain.CourseAssignment;
import com.scheduler.domain.Schedule;
import com.scheduler.domain.TimeSlot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes schedule to file and console.
 */
public class ScheduleWriter {

    /**
     * Writes schedule to console in a formatted table.
     */
    public void writeToConsole(Schedule schedule) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("COURSE TIMETABLE SCHEDULE");
        System.out.println("=".repeat(100));
        System.out.println();

        if (schedule.isEmpty()) {
            System.out.println("No courses scheduled.");
            return;
        }

        List<CourseAssignment> sortedAssignments = schedule.getAssignments().stream()
            .sorted(Comparator.comparing(a -> a.getTimeSlots().get(0)))
            .collect(Collectors.toList());

        System.out.printf("%-10s %-35s %-15s %-15s %-30s%n",
            "Course ID", "Course Name", "Room", "Professor", "Time Slots");
        System.out.println("-".repeat(100));

        for (CourseAssignment assignment : sortedAssignments) {
            String timeSlotStr = assignment.getTimeSlots().stream()
                .map(TimeSlot::toString)
                .collect(Collectors.joining(", "));

            System.out.printf("%-10s %-35s %-15s %-15s %-30s%n",
                assignment.getCourse().getId(),
                truncate(assignment.getCourse().getName(), 35),
                assignment.getRoom().getId(),
                assignment.getCourse().getProfessorId(),
                truncate(timeSlotStr, 30));
        }

        System.out.println("-".repeat(100));
        System.out.printf("Total courses scheduled: %d%n", schedule.getScheduledCoursesCount());
        System.out.println("=".repeat(100));
    }

    /**
     * Writes schedule to file.
     */
    public void writeToFile(Schedule schedule, Path outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            writer.write("Course ID,Course Name,Room ID,Room Name,Professor ID,Time Slots\n");

            List<CourseAssignment> sortedAssignments = schedule.getAssignments().stream()
                .sorted(Comparator.comparing(a -> a.getTimeSlots().get(0)))
                .collect(Collectors.toList());

            for (CourseAssignment assignment : sortedAssignments) {
                String timeSlotStr = assignment.getTimeSlots().stream()
                    .map(TimeSlot::toString)
                    .collect(Collectors.joining("; "));

                writer.write(String.format("%s,%s,%s,%s,%s,\"%s\"%n",
                    assignment.getCourse().getId(),
                    escapeCSV(assignment.getCourse().getName()),
                    assignment.getRoom().getId(),
                    escapeCSV(assignment.getRoom().getName()),
                    assignment.getCourse().getProfessorId(),
                    timeSlotStr));
            }
        }
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
