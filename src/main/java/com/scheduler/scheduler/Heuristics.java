package main.java.com.scheduler.scheduler;

import com.scheduler.domain.Course;
import com.scheduler.domain.Room;
import com.scheduler.domain.TimeSlot;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Heuristics for ordering variables and values in the scheduling algorithm.
 */
public class Heuristics {

    /**
     * Orders courses by difficulty (most constrained first).
     * Considers: enrollment size, duration, number of required features.
     */
    public static List<Course> orderCoursesByDifficulty(List<Course> courses) {
        return courses.stream()
            .sorted(Comparator
                .comparingInt(Course::getExpectedEnrollment).reversed()
                .thenComparingInt(Course::getDuration).reversed()
                .thenComparingInt((Course c) -> c.getRequiredFeatures().size()).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Orders rooms by best fit for a course.
     * Prefers rooms that closely match capacity and have required features.
     */
    public static List<Room> orderRoomsByFit(List<Room> rooms, Course course) {
        return rooms.stream()
            .filter(room -> room.canAccommodate(course.getExpectedEnrollment()))
            .filter(room -> room.hasAllFeatures(course.getRequiredFeatures()))
            .sorted(Comparator
                .comparingInt((Room r) -> r.getCapacity() - course.getExpectedEnrollment())
                .thenComparingInt((Room r) -> r.getFeatures().size()))
            .collect(Collectors.toList());
    }

    /**
     * Orders time slots, preferring earlier slots and preferred windows.
     */
    public static List<TimeSlot> orderTimeSlots(List<TimeSlot> timeSlots, Course course) {
        return timeSlots.stream()
            .sorted(Comparator
                .comparing((TimeSlot ts) -> !course.getPreferredTimeWindows().contains(ts))
                .thenComparing(TimeSlot::compareTo))
            .collect(Collectors.toList());
    }

    /**
     * Calculates a difficulty score for a course.
     */
    public static int calculateCourseDifficulty(Course course) {
        int score = 0;
        score += course.getExpectedEnrollment();
        score += course.getDuration() * 10;
        score += course.getRequiredFeatures().size() * 5;
        if (course.hasPreferredTimeWindows()) {
            score += 10;
        }
        return score;
    }
}
