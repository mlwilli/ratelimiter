
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

/**
 * Configuration for a rate limiter instance.
 */
public final class RateLimitConfig {

    private final int maxRequests;
    private final Duration window;
    private final Clock clock;

    private RateLimitConfig(Builder builder) {
        this.maxRequests = builder.maxRequests;
        this.window = builder.window;
        this.clock = builder.clock != null ? builder.clock : Clock.systemUTC();

        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be > 0");
        }
        if (window == null || window.isNegative() || window.isZero()) {
            throw new IllegalArgumentException("window must be positive");
        }
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public Duration getWindow() {
        return window;
    }

    public Clock getClock() {
        return clock;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxRequests;
        private Duration window;
        private Clock clock;

        private Builder() {
        }

        public Builder maxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
            return this;
        }

        public Builder window(Duration window) {
            this.window = window;
            return this;
        }

        /**
         * Optional: override the clock (useful for tests).
         */
        public Builder clock(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
            return this;
        }

        public RateLimitConfig build() {
            return new RateLimitConfig(this);
        }
    }
}
