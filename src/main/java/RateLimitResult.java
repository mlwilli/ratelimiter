
import java.time.Instant;

/**
 * Result of a rate limit attempt.
 */
public final class RateLimitResult {

    private final boolean allowed;
    private final int remainingTokens;
    private final Instant retryAt;

    private RateLimitResult(boolean allowed, int remainingTokens, Instant retryAt) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.retryAt = retryAt;
    }

    /**
     * @return whether the request is allowed under the current rate limit.
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * @return remaining tokens in the current window, or 0 if blocked.
     */
    public int getRemainingTokens() {
        return remainingTokens;
    }

    /**
     * @return time at which the caller may safely retry if blocked, may be null when allowed.
     */
    public Instant getRetryAt() {
        return retryAt;
    }

    public static RateLimitResult allowed(int remainingTokens) {
        return new RateLimitResult(true, remainingTokens, null);
    }

    public static RateLimitResult blocked(Instant retryAt) {
        return new RateLimitResult(false, 0, retryAt);
    }

    @Override
    public String toString() {
        return "RateLimitResult{" +
                "allowed=" + allowed +
                ", remainingTokens=" + remainingTokens +
                ", retryAt=" + retryAt +
                '}';
    }
}
