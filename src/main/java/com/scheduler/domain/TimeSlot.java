package com.scheduler.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a discrete time slot in the schedule.
 * Immutable value object with proper equals/hashCode for use in collections.
 */
public final class TimeSlot implements Comparable<TimeSlot> {
    private final int slotIndex;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    private TimeSlot(Builder builder) {
        this.slotIndex = builder.slotIndex;
        this.dayOfWeek = Objects.requireNonNull(builder.dayOfWeek, "Day of week cannot be null");
        this.startTime = Objects.requireNonNull(builder.startTime, "Start time cannot be null");
        this.endTime = Objects.requireNonNull(builder.endTime, "End time cannot be null");
        
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     */
    public boolean overlapsWith(TimeSlot other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    /**
     * Checks if this time slot is consecutive with another (immediately follows).
     */
    public boolean isConsecutiveWith(TimeSlot other) {
        return this.dayOfWeek.equals(other.dayOfWeek) 
            && this.endTime.equals(other.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return slotIndex == timeSlot.slotIndex 
            && dayOfWeek == timeSlot.dayOfWeek 
            && startTime.equals(timeSlot.startTime) 
            && endTime.equals(timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotIndex, dayOfWeek, startTime, endTime);
    }

    @Override
    public int compareTo(TimeSlot other) {
        int dayCompare = this.dayOfWeek.compareTo(other.dayOfWeek);
        if (dayCompare != 0) return dayCompare;
        
        int startCompare = this.startTime.compareTo(other.startTime);
        if (startCompare != 0) return startCompare;
        
        return Integer.compare(this.slotIndex, other.slotIndex);
    }

    @Override
    public String toString() {
        return String.format("%s %s-%s (slot %d)", 
            dayOfWeek, startTime, endTime, slotIndex);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int slotIndex;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

        public Builder slotIndex(int slotIndex) {
            this.slotIndex = slotIndex;
            return this;
        }

        public Builder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public TimeSlot build() {
            return new TimeSlot(this);
        }
    }
}