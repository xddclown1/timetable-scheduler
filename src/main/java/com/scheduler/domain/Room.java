package com.scheduler.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a physical room where courses can be scheduled.
 * Immutable with defensive copying of collections.
 */
public final class Room {
    private final String id;
    private final String name;
    private final int capacity;
    private final Set<String> features;
    private final Set<TimeSlot> unavailableTimeSlots;

    private Room(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "Room ID cannot be null");
        this.name = Objects.requireNonNull(builder.name, "Room name cannot be null");
        this.capacity = builder.capacity;
        this.features = Collections.unmodifiableSet(new HashSet<>(builder.features));
        this.unavailableTimeSlots = Collections.unmodifiableSet(
            new HashSet<>(builder.unavailableTimeSlots));
        
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be positive");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<String> getFeatures() {
        return features;
    }

    public Set<TimeSlot> getUnavailableTimeSlots() {
        return unavailableTimeSlots;
    }

    public boolean hasAllFeatures(Set<String> requiredFeatures) {
        return this.features.containsAll(requiredFeatures);
    }

    public boolean isAvailableAt(TimeSlot timeSlot) {
        return unavailableTimeSlots.stream()
            .noneMatch(unavailable -> unavailable.overlapsWith(timeSlot));
    }

    public boolean canAccommodate(int expectedEnrollment) {
        return this.capacity >= expectedEnrollment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Room{id='%s', name='%s', capacity=%d, features=%s}", 
            id, name, capacity, features);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private int capacity;
        private Set<String> features = new HashSet<>();
        private Set<TimeSlot> unavailableTimeSlots = new HashSet<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder features(Set<String> features) {
            this.features = new HashSet<>(features);
            return this;
        }

        public Builder addFeature(String feature) {
            this.features.add(feature);
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

        public Room build() {
            return new Room(this);
        }
    }
}