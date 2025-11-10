package com.scheduler.constraints;

import com.scheduler.domain.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Ensures room has all required features for the course.
 */
public class RoomFeaturesConstraint implements Constraint {

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        if (room.hasAllFeatures(course.getRequiredFeatures())) {
            return ValidationResult.success(getName());
        }

        Set<String> missingFeatures = new HashSet<>(course.getRequiredFeatures());
        missingFeatures.removeAll(room.getFeatures());

        String message = String.format(
            "Room %s is missing required features for course %s: %s",
            room.getId(), course.getId(), missingFeatures
        );

        return ValidationResult.failure(getName(), message);
    }

    @Override
    public String getName() {
        return "Room Features";
    }

    @Override
    public boolean isHardConstraint() {
        return true;
    }
}
