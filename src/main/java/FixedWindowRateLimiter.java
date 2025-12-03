
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe fixed-window rate limiter implementation.
 *
 * For each key, requests are counted in a discrete time window.
 * When the count exceeds maxRequests, further requests are rejected
 * until the next window.
 */
public final class FixedWindowRateLimiter implements RateLimiter {

    private final RateLimitConfig config;
    private final ConcurrentMap<String, WindowState> stateByKey = new ConcurrentHashMap<>();

    /**
     * Constructs a rate limiter with the specified configuration.
     */
    public FixedWindowRateLimiter(RateLimitConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    @Override
    public RateLimitResult tryConsume(String key) {
        Objects.requireNonNull(key, "key");

        long nowMillis = config.getClock().millis();
        long windowMillis = config.getWindow().toMillis();
        if (windowMillis <= 0) {
            throw new IllegalStateException("Window must be positive");
        }

        // Calculate start of the current window
        long currentWindowStart = nowMillis - (nowMillis % windowMillis);

        // Get or create per-key state
        WindowState state = stateByKey.computeIfAbsent(key, k -> new WindowState(currentWindowStart));

        state.lock.lock();
        try {
            // If we've moved into a new window, reset counter
            if (state.windowStartMillis != currentWindowStart) {
                state.windowStartMillis = currentWindowStart;
                state.counter = 0;
            }

            if (state.counter < config.getMaxRequests()) {
                state.counter++;
                int remaining = config.getMaxRequests() - state.counter;
                return RateLimitResult.allowed(remaining);
            } else {
                // Blocked: compute when the caller can try again
                long retryAtMillis = state.windowStartMillis + windowMillis;
                return RateLimitResult.blocked(Instant.ofEpochMilli(retryAtMillis));
            }
        } finally {
            state.lock.unlock();
        }
    }

    /**
     * Simple per-key state with a lock to avoid contention on a global lock.
     */
    private static final class WindowState {
        final ReentrantLock lock = new ReentrantLock();
        volatile long windowStartMillis;
        int counter;

        WindowState(long windowStartMillis) {
            this.windowStartMillis = windowStartMillis;
            this.counter = 0;
        }
    }
}
