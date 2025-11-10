package main.java.com.scheduler.constraints;

import com.scheduler.domain.*;

/**
 * Interface for constraint checking in course scheduling.
 * Each constraint validates a specific aspect of a course assignment.
 */
public interface Constraint {
    
    /**
     * Validates whether the proposed assignment satisfies this constraint.
     *
     * @param course the course to be assigned
     * @param room the room for the assignment
     * @param timeSlot the time slot for the assignment
     * @param professor the professor teaching the course
     * @param schedule the current schedule state
     * @return validation result with success/failure and messages
     */
    ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    );
    
    /**
     * Returns the name of this constraint.
     */
    String getName();
    
    /**
     * Returns whether this is a hard constraint (must be satisfied)
     * or soft constraint (preferred but not required).
     */
    boolean isHardConstraint();
}
