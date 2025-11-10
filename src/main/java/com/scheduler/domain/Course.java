package main.java.com.scheduler.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a course that needs to be scheduled.
 * Immutable with defensive copying of collections.
 */
public final class Course {
    private final String id;
    private final String name;
    private final int duration;
    private final int expectedEnrollment;
    private final Set<String> requiredFeatures;
    private final Set<TimeSlot> preferredTimeWindows;
    private final String professorId;

    private Course(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Course ID cannot be null");
        this.name = Objects.requireNonNull(builder.name, "Course name cannot be null");
        this.duration = builder.duration;
        this.expectedEnrollment = builder.expectedEnrollment;
        this.requiredFeatures = Collections.unmodifiableSet(
            new HashSet<>(builder.requiredFeatures));
        this.preferredTimeWindows = Collections.unmodifiableSet(
            new HashSet<>(builder.preferredTimeWindows));
        this.professorId = Objects.requireNonNull(builder.professorId, 
            "Professor ID cannot be null");
        
        if (duration < 1) {
            throw new IllegalArgumentException("Course duration must be at least 1 slot");
        }
        if (expectedEnrollment < 1) {
            throw new IllegalArgumentException("Expected enrollment must be positive");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getExpectedEnrollment() {
        return expectedEnrollment;
    }

    public Set<String> getRequiredFeatures() {
        return requiredFeatures;
    }

    public Set<TimeSlot> getPreferredTimeWindows() {
        return preferredTimeWindows;
    }

    public String getProfessorId() {
        return professorId;
    }

    public boolean hasPreferredTimeWindows() {
        return !preferredTimeWindows.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Course{id='%s', name='%s', duration=%d, enrollment=%d, prof=%s}", 
            id, name, duration, expectedEnrollment, professorId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private int duration;
        private int expectedEnrollment;
        private Set<String> requiredFeatures = new HashSet<>();
        private Set<TimeSlot> preferredTimeWindows = new HashSet<>();
        private String professorId;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder expectedEnrollment(int expectedEnrollment) {
            this.expectedEnrollment = expectedEnrollment;
            return this;
        }

        public Builder requiredFeatures(Set<String> requiredFeatures) {
            this.requiredFeatures = new HashSet<>(requiredFeatures);
            return this;
        }

        public Builder addRequiredFeature(String feature) {
            this.requiredFeatures.add(feature);
            return this;
        }

        public Builder preferredTimeWindows(Set<TimeSlot> preferredTimeWindows) {
            this.preferredTimeWindows = new HashSet<>(preferredTimeWindows);
            return this;
        }

        public Builder addPreferredTimeWindow(TimeSlot timeSlot) {
            this.preferredTimeWindows.add(timeSlot);
            return this;
        }

        public Builder professorId(String professorId) {
            this.professorId = professorId;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}