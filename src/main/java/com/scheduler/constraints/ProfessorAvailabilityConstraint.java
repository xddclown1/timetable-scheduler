package com.scheduler.constraints;


import com.scheduler.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ensures professors are not double-booked and respects their unavailable time slots.
 */
public class ProfessorAvailabilityConstraint implements Constraint {

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        List<String> violations = new ArrayList<>();

        // Check if professor is unavailable at this time
        if (!professor.isAvailableAt(timeSlot)) {
            violations.add(String.format(
                "Professor %s (%s) is unavailable at %s",
                professor.getId(), professor.getName(), timeSlot
            ));
        }

        // Check if professor is already teaching another course at this time
        if (!schedule.isProfessorAvailableAt(professor.getId(), timeSlot)) {
            violations.add(String.format(
                "Professor %s (%s) is already scheduled at %s",
                professor.getId(), professor.getName(), timeSlot
            ));
        }

        if (violations.isEmpty()) {
            return ValidationResult.success(getName());
        }
        return ValidationResult.failure(getName(), violations);
    }

    @Override
    public String getName() {
        return "Professor Availability";
    }

    @Override
    public boolean isHardConstraint() {
        return true;
    }
}
