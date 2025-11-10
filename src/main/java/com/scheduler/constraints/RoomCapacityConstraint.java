package main.java.com.scheduler.constraints;

import com.scheduler.domain.*;

/**
 * Ensures room capacity is sufficient for expected course enrollment.
 */
public class RoomCapacityConstraint implements Constraint {

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        if (room.canAccommodate(course.getExpectedEnrollment())) {
            return ValidationResult.success(getName());
        }

        String message = String.format(
            "Room %s (capacity %d) cannot accommodate course %s (enrollment %d)",
            room.getId(), room.getCapacity(),
            course.getId(), course.getExpectedEnrollment()
        );

        return ValidationResult.failure(getName(), message);
    }

    @Override
    public String getName() {
        return "Room Capacity";
    }

    @Override
    public boolean isHardConstraint() {
        return true;
    }
}
