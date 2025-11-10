package com.scheduler.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration holder for application settings.
 */
public class SchedulerConfiguration {
    private Path coursesFile;
    private Path professorsFile;
    private Path roomsFile;
    private Path timeSlotsFile;
    private Path outputFile;
    private boolean verbose;
    private boolean softPreferences;
    private long timeoutSeconds;
    private long seed;
    private int maxIterations;

    private SchedulerConfiguration(Builder builder) {
        this.coursesFile = builder.coursesFile;
        this.professorsFile = builder.professorsFile;
        this.roomsFile = builder.roomsFile;
        this.timeSlotsFile = builder.timeSlotsFile;
        this.outputFile = builder.outputFile;
        this.verbose = builder.verbose;
        this.softPreferences = builder.softPreferences;
        this.timeoutSeconds = builder.timeoutSeconds;
        this.seed = builder.seed;
        this.maxIterations = builder.maxIterations;
    }

    public Path getCoursesFile() {
        return coursesFile;
    }

    public Path getProfessorsFile() {
        return professorsFile;
    }

    public Path getRoomsFile() {
        return roomsFile;
    }

    public Path getTimeSlotsFile() {
        return timeSlotsFile;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isSoftPreferences() {
        return softPreferences;
    }

    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public long getSeed() {
        return seed;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Path coursesFile = Paths.get("sample-data/courses.csv");
        private Path professorsFile = Paths.get("sample-data/professors.csv");
        private Path roomsFile = Paths.get("sample-data/rooms.csv");
        private Path timeSlotsFile = Paths.get("sample-data/timeslots.csv");
        private Path outputFile = Paths.get("schedule.txt");
        private boolean verbose = false;
        private boolean softPreferences = false;
        private long timeoutSeconds = 60;
        private long seed = System.currentTimeMillis();
        private int maxIterations = 10000;

        public Builder coursesFile(Path coursesFile) {
            this.coursesFile = coursesFile;
            return this;
        }

        public Builder professorsFile(Path professorsFile) {
            this.professorsFile = professorsFile;
            return this;
        }

        public Builder roomsFile(Path roomsFile) {
            this.roomsFile = roomsFile;
            return this;
        }

        public Builder timeSlotsFile(Path timeSlotsFile) {
            this.timeSlotsFile = timeSlotsFile;
            return this;
        }

        public Builder outputFile(Path outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public Builder softPreferences(boolean softPreferences) {
            this.softPreferences = softPreferences;
            return this;
        }

        public Builder timeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public SchedulerConfiguration build() {
            return new SchedulerConfiguration(this);
        }
    }
}
