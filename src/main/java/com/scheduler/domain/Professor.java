package main.java.com.scheduler.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a professor who teaches courses.
 * Immutable with defensive copying of collections.
 */
public final class Professor {
    private final String id;
    private final String name;
    private final Set<TimeSlot> unavailableTimeSlots;
    private final int maxLoad;

    private Professor(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Professor ID cannot be null");
        this.name = Objects.requireNonNull(builder.name, "Professor name cannot be null");
        this.unavailableTimeSlots = Collections.unmodifiableSet(
            new HashSet<>(builder.unavailableTimeSlots));
        this.maxLoad = builder.maxLoad;
        
        if (maxLoad < 0) {
            throw new IllegalArgumentException("Max load cannot be negative");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<TimeSlot> getUnavailableTimeSlots() {
        return unavailableTimeSlots;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public boolean isAvailableAt(TimeSlot timeSlot) {
        return unavailableTimeSlots.stream()
            .noneMatch(unavailable -> unavailable.overlapsWith(timeSlot));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return id.equals(professor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Professor{id='%s', name='%s', maxLoad=%d}", 
            id, name, maxLoad);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private Set<TimeSlot> unavailableTimeSlots = new HashSet<>();
        private int maxLoad = Integer.MAX_VALUE;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder unavailableTimeSlots(Set<TimeSlot> unavailableTimeSlots) {
            this.unavailableTimeSlots = new HashSet<>(unavailableTimeSlots);
            return this;
        }

        public Builder addUnavailableTimeSlot(TimeSlot timeSlot) {
            this.unavailableTimeSlots.add(timeSlot);
            return this;
        }

        public Builder maxLoad(int maxLoad) {
            this.maxLoad = maxLoad;
            return this;
        }

        public Professor build() {
            return new Professor(this);
        }
    }
}