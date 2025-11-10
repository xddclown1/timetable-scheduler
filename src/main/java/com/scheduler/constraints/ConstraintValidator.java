package com.scheduler.constraints;

import com.scheduler.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validates course assignments against a collection of constraints.
 * Stateless validator with composable constraint checks.
 */
public class ConstraintValidator {
    
    private final List<Constraint> constraints;
    private final boolean treatSoftConstraintsAsHard;

    public ConstraintValidator(List<Constraint> constraints, boolean treatSoftConstraintsAsHard) {
        this.constraints = new ArrayList<>(Objects.requireNonNull(constraints));
        this.treatSoftConstraintsAsHard = treatSoftConstraintsAsHard;
    }

    public ConstraintValidator(List<Constraint> constraints) {
        this(constraints, false);
    }

    /**
     * Creates a validator with default constraints.
     */
    public static ConstraintValidator withDefaultConstraints() {
        List<Constraint> defaultConstraints = List.of(
            new ProfessorAvailabilityConstraint(),
            new RoomAvailabilityConstraint(),
            new RoomCapacityConstraint(),
            new RoomFeaturesConstraint(),
            new PreferredTimeWindowConstraint()
        );
        return new ConstraintValidator(defaultConstraints);
    }

    /**
     * Validates a single time slot assignment.
     */
    public ConstraintValidationResult validate(
        Course course,
        Room room,
        TimeSlot timeSlot,
        Professor professor,
        Schedule schedule
    ) {
        List<ValidationResult> results = new ArrayList<>();
        boolean allHardConstraintsSatisfied = true;

        for (Constraint constraint : constraints) {
            ValidationResult result = constraint.validate(course, room, timeSlot, professor, schedule);
            results.add(result);

            if (!result.isValid()) {
                if (constraint.isHardConstraint() || treatSoftConstraintsAsHard) {
                    allHardConstraintsSatisfied = false;
                }
            }
        }

        return new ConstraintValidationResult(allHardConstraintsSatisfied, results);
    }

    /**
     * Validates a multi-slot assignment.
     */
    public ConstraintValidationResult validateMultiSlot(
        Course course,
        Room room,
        List<TimeSlot> timeSlots,
        Professor professor,
        Schedule schedule
    ) {
        List<ValidationResult> results = new ArrayList<>();
        boolean allHardConstraintsSatisfied = true;

        // Validate consecutive slots constraint
        ConsecutiveSlotsConstraint consecutiveConstraint = new ConsecutiveSlotsConstraint();
        ValidationResult consecutiveResult = consecutiveConstraint.validateMultiSlot(
            course, room, timeSlots, professor, schedule
        );
        results.add(consecutiveResult);

        if (!consecutiveResult.isValid()) {
            allHardConstraintsSatisfied = false;
        }

        // Validate each individual slot
        for (TimeSlot timeSlot : timeSlots) {
            ConstraintValidationResult slotResult = validate(
                course, room, timeSlot, professor, schedule
            );
            results.addAll(slotResult.getResults());

            if (!slotResult.isValid()) {
                allHardConstraintsSatisfied = false;
            }
        }

        return new ConstraintValidationResult(allHardConstraintsSatisfied, results);
    }

    /**
     * Result of constraint validation with detailed information.
     */
    public static class ConstraintValidationResult {
        private final boolean valid;
        private final List<ValidationResult> results;

        public ConstraintValidationResult(boolean valid, List<ValidationResult> results) {
            this.valid = valid;
            this.results = new ArrayList<>(results);
        }

        public boolean isValid() {
            return valid;
        }

        public List<ValidationResult> getResults() {
            return new ArrayList<>(results);
        }

        public List<ValidationResult> getFailedResults() {
            return results.stream()
                .filter(r -> !r.isValid())
                .toList();
        }

        public List<String> getAllMessages() {
            return results.stream()
                .flatMap(r -> r.getMessages().stream())
                .toList();
        }

        @Override
        public String toString() {
            if (valid) {
                return "All constraints satisfied";
            }
            return "Constraint violations:\n" + 
                String.join("\n", getAllMessages());
        }
    }
}
