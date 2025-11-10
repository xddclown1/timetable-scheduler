package com.scheduler.scheduler;

import com.scheduler.constraints.ConstraintValidator;
import com.scheduler.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Backtracking scheduler with constraint satisfaction and heuristics.
 */
public class BacktrackingScheduler implements Scheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(BacktrackingScheduler.class);

    private final SchedulerConfig config;
    private final ConstraintValidator validator;
    private long startTime;
    private int iterationCount;

    public BacktrackingScheduler(SchedulerConfig config, ConstraintValidator validator) {
        this.config = config;
        this.validator = validator;
    }

    public BacktrackingScheduler(SchedulerConfig config) {
        this(config, ConstraintValidator.withDefaultConstraints());
    }

    @Override
    public ScheduleResult schedule(
        List<Course> courses,
        List<Professor> professors,
        List<Room> rooms,
        List<TimeSlot> timeSlots
    ) {
        startTime = System.currentTimeMillis();
        iterationCount = 0;

        logger.info("Starting scheduling process for {} courses", courses.size());

        // Create lookup maps
        Map<String, Professor> professorMap = professors.stream()
            .collect(Collectors.toMap(Professor::getId, p -> p));

        // Order courses by difficulty
        List<Course> orderedCourses = Heuristics.orderCoursesByDifficulty(courses);

        Schedule schedule = new Schedule();
        List<String> unscheduledCourses = new ArrayList<>();
        List<String> messages = new ArrayList<>();

        boolean success = backtrack(
            orderedCourses,
            0,
            schedule,
            professorMap,
            rooms,
            timeSlots,
            unscheduledCourses,
            messages
        );

        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("Scheduling completed in {} ms. Success: {}, Scheduled: {}/{}", 
            executionTime, success, schedule.getScheduledCoursesCount(), courses.size());

        return ScheduleResult.builder()
            .success(success)
            .schedule(schedule)
            .unscheduledCourses(unscheduledCourses)
            .messages(messages)
            .executionTimeMillis(executionTime)
            .build();
    }

    private boolean backtrack(
        List<Course> courses,
        int courseIndex,
        Schedule schedule,
        Map<String, Professor> professorMap,
        List<Room> rooms,
        List<TimeSlot> timeSlots,
        List<String> unscheduledCourses,
        List<String> messages
    ) {
        // Check timeout
        if (System.currentTimeMillis() - startTime > config.getTimeoutMillis()) {
            messages.add("Scheduling timed out after " + config.getTimeoutMillis() + " ms");
            return false;
        }

        // Check max iterations
        if (++iterationCount > config.getMaxIterations()) {
            messages.add("Reached maximum iterations: " + config.getMaxIterations());
            return false;
        }

        // Base case: all courses scheduled
        if (courseIndex >= courses.size()) {
            return true;
        }

        Course course = courses.get(courseIndex);
        Professor professor = professorMap.get(course.getProfessorId());

        if (professor == null) {
            messages.add("Professor not found for course " + course.getId());
            unscheduledCourses.add(course.getId());
            return backtrack(courses, courseIndex + 1, schedule, professorMap, 
                rooms, timeSlots, unscheduledCourses, messages);
        }

        // Try to assign this course
        List<Room> orderedRooms = Heuristics.orderRoomsByFit(rooms, course);
        List<TimeSlot> orderedSlots = Heuristics.orderTimeSlots(timeSlots, course);

        for (Room room : orderedRooms) {
            List<List<TimeSlot>> slotCombinations = generateConsecutiveSlots(
                orderedSlots, course.getDuration());

            for (List<TimeSlot> slots : slotCombinations) {
                if (tryAssignment(course, room, slots, professor, schedule)) {
                    // Assignment successful, continue with next course
                    if (backtrack(courses, courseIndex + 1, schedule, professorMap, 
                        rooms, timeSlots, unscheduledCourses, messages)) {
                        return true;
                    }
                    // Backtrack: remove assignment
                    removeAssignment(course, schedule);
                }
            }
        }

        // Could not schedule this course
        logger.warn("Could not schedule course: {}", course.getId());
        unscheduledCourses.add(course.getId());
        messages.add("Failed to schedule course " + course.getId() + 
            " - no valid room/time combination found");

        // Try to continue with remaining courses
        return backtrack(courses, courseIndex + 1, schedule, professorMap, 
            rooms, timeSlots, unscheduledCourses, messages);
    }

    private boolean tryAssignment(
        Course course,
        Room room,
        List<TimeSlot> timeSlots,
        Professor professor,
        Schedule schedule
    ) {
        // Validate the assignment
        ConstraintValidator.ConstraintValidationResult result = 
            validator.validateMultiSlot(course, room, timeSlots, professor, schedule);

        if (!result.isValid()) {
            return false;
        }

        // Add assignment to schedule
        CourseAssignment assignment = new CourseAssignment(course, room, timeSlots);
        schedule.addAssignment(assignment);
        return true;
    }

    private void removeAssignment(Course course, Schedule schedule) {
        Optional<CourseAssignment> assignment = schedule.getAssignment(course.getId());
        assignment.ifPresent(schedule::removeAssignment);
    }

    private List<List<TimeSlot>> generateConsecutiveSlots(
        List<TimeSlot> availableSlots,
        int duration
    ) {
        if (duration == 1) {
            return availableSlots.stream()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        }

        List<List<TimeSlot>> combinations = new ArrayList<>();
        List<TimeSlot> sortedSlots = availableSlots.stream()
            .sorted()
            .collect(Collectors.toList());

        for (int i = 0; i <= sortedSlots.size() - duration; i++) {
            List<TimeSlot> candidate = new ArrayList<>();
            candidate.add(sortedSlots.get(i));

            boolean valid = true;
            for (int j = 1; j < duration; j++) {
                TimeSlot previous = sortedSlots.get(i + j - 1);
                TimeSlot current = sortedSlots.get(i + j);

                if (!previous.isConsecutiveWith(current)) {
                    valid = false;
                    break;
                }
                candidate.add(current);
            }

            if (valid) {
                combinations.add(candidate);
            }
        }

        return combinations;
    }
}
