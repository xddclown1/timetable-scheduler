package main.java.com.scheduler.constraints;

import com.scheduler.domain.*;

/**
 * Soft constraint that prefers scheduling courses in their preferred time windows.
 */
public class PreferredTimeWindowConstraint implements Constraint {

    @Override
    public ValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        if (!course.hasPreferredTimeWindows()) {
            return ValidationResult.success(getName());
        }

        boolean inPreferredWindow = course.getPreferredTimeWindows().stream()
            .anyMatch(preferred -> preferred.equals(timeSlot) || 
                                  preferred.overlapsWith(timeSlot));

        if (inPreferredWindow) {
            return ValidationResult.success(getName());
        }

        String message = String.format(
            "Course %s is not scheduled in a preferred time window",
            course.getId()
        );

        return ValidationResult.failure(getName(), message);
    }

    @Override
    public String getName() {
        return "Preferred Time Window";
    }

    @Override
    public boolean isHardConstraint() {
        return false;
    }
}
