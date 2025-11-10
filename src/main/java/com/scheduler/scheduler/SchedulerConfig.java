package main.java.com.scheduler.scheduler;

/**
 * Configuration for the scheduling algorithm.
 */
public class SchedulerConfig {
    private final boolean treatSoftConstraintsAsHard;
    private final long timeoutMillis;
    private final long seed;
    private final int maxIterations;

    private SchedulerConfig(Builder builder) {
        this.treatSoftConstraintsAsHard = builder.treatSoftConstraintsAsHard;
        this.timeoutMillis = builder.timeoutMillis;
        this.seed = builder.seed;
        this.maxIterations = builder.maxIterations;
    }

    public boolean isTreatSoftConstraintsAsHard() {
        return treatSoftConstraintsAsHard;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
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
        private boolean treatSoftConstraintsAsHard = false;
        private long timeoutMillis = 60000; // 60 seconds default
        private long seed = System.currentTimeMillis();
        private int maxIterations = 10000;

        public Builder treatSoftConstraintsAsHard(boolean value) {
            this.treatSoftConstraintsAsHard = value;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
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

        public SchedulerConfig build() {
            return new SchedulerConfig(this);
        }
    }
}
