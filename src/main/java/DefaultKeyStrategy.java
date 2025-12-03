
/**
 * Example key strategy implementation.
 * For a real HTTP stack, you might use IP + user id, etc.
 */
public final class DefaultKeyStrategy implements KeyStrategy {

    @Override
    public String computeKey(Object input) {
        if (input == null) {
            return "anonymous";
        }
        return input.toString();
    }
}
