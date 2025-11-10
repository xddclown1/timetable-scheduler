package main.java.com.scheduler.scheduler;

import com.scheduler.domain.Course;
import com.scheduler.domain.Professor;
import com.scheduler.domain.Room;
import com.scheduler.domain.TimeSlot;

import java.util.List;

/**
 * Interface for course scheduling algorithms.
 */
public interface Scheduler {
    
    /**
     * Schedules courses into rooms and time slots.
     *
     * @param courses list of courses to schedule
     * @param professors list of available professors
     * @param rooms list of available rooms
     * @param timeSlots list of available time slots
     * @return result containing the schedule and status information
     */
    ScheduleResult schedule(
        List<Course> courses,
        List<Professor> professors,
        List<Room> rooms,
        List<TimeSlot> timeSlots
    );
}
