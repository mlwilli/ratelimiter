
import java.util.Objects;

/**
 * Strategy for computing the logical rate-limit key from arbitrary inputs.
 * This allows callers to encapsulate how they derive keys (e.g. from HTTP requests).
 */
@FunctionalInterface
public interface KeyStrategy {

    /**
     * Computes a rate limiting key from an arbitrary input object.
     *
     * @param input input object (could be a user id, IP, request object etc.)
     * @return non-null logical key
     */
    String computeKey(Object input);

    /**
     * Returns a KeyStrategy that simply calls toString() on the input.
     */
    static KeyStrategy fromToString() {
        return input -> Objects.requireNonNull(input, "input").toString();
    }
}
