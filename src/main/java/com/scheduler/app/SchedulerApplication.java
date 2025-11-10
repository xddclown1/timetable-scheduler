package main.java.com.scheduler.app;

import com.scheduler.config.SchedulerConfiguration;
import com.scheduler.constraints.ConstraintValidator;
import com.scheduler.domain.*;
import com.scheduler.io.*;
import com.scheduler.scheduler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Main application entry point for the Course Timetable Scheduler.
 */
public class SchedulerApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerApplication.class);

    public static void main(String[] args) {
        try {
            // Check for help flag
            if (args.length > 0 && (args[0].equals("--help") || args[0].equals("-h"))) {
                CommandLineParser.printUsage();
                System.exit(0);
            }

            // Parse command line arguments
            SchedulerConfiguration config = CommandLineParser.parse(args);

            // Print banner
            printBanner();

            // Validate input files exist
            validateInputFiles(config);

            // Load data from files
            System.out.println("Loading data from files...");
            DataLoader dataLoader = new DataLoader(config);
            SchedulingData data = dataLoader.load();

            System.out.println("Loaded:");
            System.out.println("  - " + data.getTimeSlots().size() + " time slots");
            System.out.println("  - " + data.getProfessors().size() + " professors");
            System.out.println("  - " + data.getRooms().size() + " rooms");
            System.out.println("  - " + data.getCourses().size() + " courses");
            System.out.println();

            // Configure scheduler
            SchedulerConfig schedulerConfig = SchedulerConfig.builder()
                .treatSoftConstraintsAsHard(config.isSoftPreferences())
                .timeoutMillis(config.getTimeoutSeconds() * 1000)
                .seed(config.getSeed())
                .maxIterations(config.getMaxIterations())
                .build();

            ConstraintValidator validator = ConstraintValidator.withDefaultConstraints();
            BacktrackingScheduler scheduler = new BacktrackingScheduler(schedulerConfig, validator);

            // Run scheduling
            System.out.println("Starting scheduling process...");
            System.out.println("Configuration:");
            System.out.println("  - Timeout: " + config.getTimeoutSeconds() + " seconds");
            System.out.println("  - Seed: " + config.getSeed());
            System.out.println("  - Max iterations: " + config.getMaxIterations());
            System.out.println("  - Soft preferences as hard: " + config.isSoftPreferences());
            System.out.println();

            long startTime = System.currentTimeMillis();
            ScheduleResult result = scheduler.schedule(
                data.getCourses(),
                data.getProfessors(),
                data.getRooms(),
                data.getTimeSlots()
            );
            long endTime = System.currentTimeMillis();

            // Print results
            printResults(result, endTime - startTime);

            // Write output
            ScheduleWriter writer = new ScheduleWriter();
            
            // Write to console
            writer.writeToConsole(result.getSchedule());

            // Write to file
            if (config.getOutputFile() != null) {
                writer.writeToFile(result.getSchedule(), config.getOutputFile());
                System.out.println("\nSchedule written to: " + config.getOutputFile());
            }

            // Exit with appropriate code
            System.exit(result.isSuccess() ? 0 : 1);

        } catch (ParseException e) {
            System.err.println("Error parsing input file: " + e.getMessage());
            logger.error("Parse error", e);
            System.exit(2);
        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            logger.error("IO error", e);
            System.exit(3);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            logger.error("Unexpected error", e);
            e.printStackTrace();
            System.exit(4);
        }
    }

    private static void printBanner() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       COURSE TIMETABLE SCHEDULER WITH TDD & CSP            ║");
        System.out.println("║              Backtracking with Constraints                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void validateInputFiles(SchedulerConfiguration config) throws IOException {
        validateFile(config.getCoursesFile(), "Courses");
        validateFile(config.getProfessorsFile(), "Professors");
        validateFile(config.getRoomsFile(), "Rooms");
        validateFile(config.getTimeSlotsFile(), "Time slots");
    }

    private static void validateFile(Path file, String description) throws IOException {
        if (!Files.exists(file)) {
            throw new IOException(description + " file not found: " + file);
        }
        if (!Files.isReadable(file)) {
            throw new IOException(description + " file is not readable: " + file);
        }
    }

    private static void printResults(ScheduleResult result, long executionTime) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SCHEDULING RESULTS");
        System.out.println("=".repeat(60));
        System.out.println();

        System.out.println("Status: " + (result.isSuccess() ? "SUCCESS" : "PARTIAL"));
        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println("Courses scheduled: " + result.getSchedule().getScheduledCoursesCount());
        
        if (!result.getUnscheduledCourses().isEmpty()) {
            System.out.println("Unscheduled courses: " + result.getUnscheduledCourses().size());
            System.out.println("\nUnscheduled course IDs:");
            for (String courseId : result.getUnscheduledCourses()) {
                System.out.println("  - " + courseId);
            }
        }

        if (!result.getMessages().isEmpty()) {
            System.out.println("\nMessages:");
            for (String message : result.getMessages()) {
                System.out.println("  - " + message);
            }
        }

        System.out.println();
    }

    /**
     * Helper class to hold loaded scheduling data.
     */
    private static class SchedulingData {
        private final List<Course> courses;
        private final List<Professor> professors;
        private final List<Room> rooms;
        private final List<TimeSlot> timeSlots;

        public SchedulingData(List<Course> courses, List<Professor> professors,
                            List<Room> rooms, List<TimeSlot> timeSlots) {
            this.courses = courses;
            this.professors = professors;
            this.rooms = rooms;
            this.timeSlots = timeSlots;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public List<Professor> getProfessors() {
            return professors;
        }

        public List<Room> getRooms() {
            return rooms;
        }

        public List<TimeSlot> getTimeSlots() {
            return timeSlots;
        }
    }

    /**
     * Helper class to load all data from files.
     */
    private static class DataLoader {
        private final SchedulerConfiguration config;

        public DataLoader(SchedulerConfiguration config) {
            this.config = config;
        }

        public SchedulingData load() throws IOException, ParseException {
            // Parse time slots first (needed for other parsers)
            TimeSlotParser timeSlotParser = new TimeSlotParser();
            List<TimeSlot> timeSlots = timeSlotParser.parse(config.getTimeSlotsFile());
            Map<String, TimeSlot> timeSlotMap = timeSlotParser.getSlotIdMap();

            // Parse professors
            ProfessorParser professorParser = new ProfessorParser(timeSlotMap);
            List<Professor> professors = professorParser.parse(config.getProfessorsFile());

            // Parse rooms
            RoomParser roomParser = new RoomParser(timeSlotMap);
            List<Room> rooms = roomParser.parse(config.getRoomsFile());

            // Parse courses
            CourseParser courseParser = new CourseParser(timeSlotMap);
            List<Course> courses = courseParser.parse(config.getCoursesFile());

            return new SchedulingData(courses, professors, rooms, timeSlots);
        }
    }
}
