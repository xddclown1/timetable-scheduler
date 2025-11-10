package main.java.com.scheduler.constraints;

import com.scheduler.domain.*;

import java.util.List;

/**
 * Ensures multi-slot courses are scheduled in consecutive time slots.
 */
public class ConsecutiveSlotsConstraint implements Constraint {

    /**
     * Validates that all time slots for a multi-slot course are consecutive.
     * This method should be called with all time slots for the course.
     */
    public ValidationResult validateMultiSlot(
        Course course,
        Room room,
        List<TimeSlot> timeSlots,
        Professor professor,
        Schedule schedule
    ) {
        if (timeSlots.size() != course.getDuration()) {
            String message = String.format(
                "Course %s requires %d slots but %d were provided",
                course.getId(), course.getDuration(), timeSlots.size()
            );
            return ValidationResult.failure(getName(), message);
        }

        if (timeSlots.size() == 1) {
            return ValidationResult.success(getName());
        }

        // Check if all slots are consecutive
        for (int i = 0; i < timeSlots.size() - 1; i++) {
            if (!timeSlots.get(i).isConsecutiveWith(timeSlots.get(i + 1))) {
                String message = String.format(
                    "Course %s requires consecutive slots, but slots %d and %d are not consecutive",
                    course.getId(), i, i + 1
                );
                return ValidationResult.failure(getName(), message);
            }
        }

        return ValidationResult.success(getName());
    }

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        // Single slot validation always passes
        return ValidationResult.success(getName());
    }

    @Override
    public String getName() {
        return "Consecutive Slots";
    }

    @Override
    public boolean isHardConstraint() {
        return true;
    }
}
