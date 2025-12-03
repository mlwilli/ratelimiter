
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Minimal demonstration of the rate limiter in action.
 * This can be used as a quick manual test or example for the README.
 */
public final class Demo {

    public static void main(String[] args) throws InterruptedException {
        RateLimitConfig config = RateLimitConfig.builder()
                .maxRequests(5)
                .window(Duration.ofSeconds(10))
                .build();

        RateLimiter rateLimiter = new FixedWindowRateLimiter(config);

        String key = "user-123";

        System.out.println("Rate limiting key: " + key);
        System.out.println("Allowed requests: " + config.getMaxRequests() +
                " per " + config.getWindow().getSeconds() + " seconds\n");

        for (int i = 1; i <= 7; i++) {
            RateLimitResult result = rateLimiter.tryConsume(key);

            System.out.printf("Call %d -> allowed=%s, remaining=%d",
                    i, result.isAllowed(), result.getRemainingTokens());

            if (!result.isAllowed() && result.getRetryAt() != null) {
                String formatted = DateTimeFormatter.ISO_INSTANT.format(result.getRetryAt());
                System.out.printf(", retryAt=%s%n", formatted);
            } else {
                System.out.println();
            }

            // Sleep a little just so timestamps are easier to see if you log them.
            Thread.sleep(300);
        }

        System.out.println("\nWaiting long enough for the next window...");
        Thread.sleep(config.getWindow().toMillis() + 500);

        System.out.println("New window begins:");
        for (int i = 1; i <= 3; i++) {
            RateLimitResult result = rateLimiter.tryConsume(key);
            System.out.printf("Call %d in new window -> allowed=%s, remaining=%d%n",
                    i, result.isAllowed(), result.getRemainingTokens());
        }
    }

    private Demo() {
        // utility class
    }
}
