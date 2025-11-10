package com.scheduler.scheduler;

import com.scheduler.domain.Schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a scheduling attempt.
 */
public class ScheduleResult {
    private final boolean success;
    private final Schedule schedule;
    private final List<String> unscheduledCourses;
    private final List<String> messages;
    private final long executionTimeMillis;

    private ScheduleResult(Builder builder) {
        this.success = builder.success;
        this.schedule = builder.schedule;
        this.unscheduledCourses = Collections.unmodifiableList(
            new ArrayList<>(builder.unscheduledCourses));
        this.messages = Collections.unmodifiableList(new ArrayList<>(builder.messages));
        this.executionTimeMillis = builder.executionTimeMillis;
    }

    public boolean isSuccess() {
        return success;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public List<String> getUnscheduledCourses() {
        return unscheduledCourses;
    }

    public List<String> getMessages() {
        return messages;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private Schedule schedule;
        private List<String> unscheduledCourses = new ArrayList<>();
        private List<String> messages = new ArrayList<>();
        private long executionTimeMillis;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder schedule(Schedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public Builder unscheduledCourses(List<String> unscheduledCourses) {
            this.unscheduledCourses = new ArrayList<>(unscheduledCourses);
            return this;
        }

        public Builder addUnscheduledCourse(String courseId) {
            this.unscheduledCourses.add(courseId);
            return this;
        }

        public Builder messages(List<String> messages) {
            this.messages = new ArrayList<>(messages);
            return this;
        }

        public Builder addMessage(String message) {
            this.messages.add(message);
            return this;
        }

        public Builder executionTimeMillis(long executionTimeMillis) {
            this.executionTimeMillis = executionTimeMillis;
            return this;
        }

        public ScheduleResult build() {
            return new ScheduleResult(this);
        }
    }
}
