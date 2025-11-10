package main.java.com.scheduler.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a constraint validation.
 * Immutable value object.
 */
public final class ValidationResult {
    private final boolean valid;
    private final List<String> messages;
    private final String constraintName;

    private ValidationResult(boolean valid, String constraintName, List<String> messages) {
        this.valid = valid;
        this.constraintName = Objects.requireNonNull(constraintName);
        this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public static ValidationResult success(String constraintName) {
        return new ValidationResult(true, constraintName, List.of());
    }

    public static ValidationResult failure(String constraintName, String message) {
        return new ValidationResult(false, constraintName, List.of(message));
    }

    public static ValidationResult failure(String constraintName, List<String> messages) {
        return new ValidationResult(false, constraintName, messages);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getConstraintName() {
        return constraintName;
    }

    @Override
    public String toString() {
        if (valid) {
            return String.format("%s: PASSED", constraintName);
        }
        return String.format("%s: FAILED - %s", constraintName, String.join(", ", messages));
    }
}
