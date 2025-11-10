package com.scheduler.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an assignment of a course to a room and time slots.
 * Immutable value object.
 */
public final class CourseAssignment {
    private final Course course;
    private final Room room;
    private final List<TimeSlot> timeSlots;

    public CourseAssignment(Course course, Room room, List<TimeSlot> timeSlots) {
        this.course = Objects.requireNonNull(course, "Course cannot be null");
        this.room = Objects.requireNonNull(room, "Room cannot be null");
        
        if (timeSlots == null || timeSlots.isEmpty()) {
            throw new IllegalArgumentException("Time slots cannot be null or empty");
        }
        if (timeSlots.size() != course.getDuration()) {
            throw new IllegalArgumentException(
                String.format("Time slots size (%d) must match course duration (%d)", 
                    timeSlots.size(), course.getDuration()));
        }
        
        this.timeSlots = Collections.unmodifiableList(new ArrayList<>(timeSlots));
    }

    public Course getCourse() {
        return course;
    }

    public Room getRoom() {
        return room;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseAssignment that = (CourseAssignment) o;
        return course.equals(that.course) 
            && room.equals(that.room) 
            && timeSlots.equals(that.timeSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, room, timeSlots);
    }

    @Override
    public String toString() {
        return String.format("Assignment{course=%s, room=%s, slots=%s}", 
            course.getId(), room.getId(), timeSlots);
    }
}