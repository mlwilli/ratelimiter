/**
 * High-level contract for rate limiting.
 * Implementations are responsible for concurrency-safety.
 */
public interface RateLimiter {

    /**
     * Attempts to consume a single token for the given key.
     *
     * @param key logical identifier to rate limit on (e.g. user id, IP)
     * @return result indicating whether the request is allowed and metadata about remaining quota
     */
    RateLimitResult tryConsume(String key);
}
