package Implementation;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The {@code Timestamp} class represents a timestamp with both formatted
 * string representation and Unix time (epoch seconds).
 * This class provides utilities for parsing, formatting, and retrieving timestamps.
 */
public class Timestamp {
    private final String timestamp;
    private final long unixTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(ZoneId.systemDefault());

    /**
     * Constructs a {@code Timestamp} object from a given timestamp string.
     *
     * @param timestamp A timestamp string in ISO-8601 format.
     * @throws DateTimeParseException if the string cannot be parsed as an Instant.
     */
    public Timestamp(String timestamp) {
        this.timestamp = timestamp;
        this.unixTime = Instant.parse(timestamp).getEpochSecond();
    }

    /**
     * Creates a {@code Timestamp} object representing the current system time.
     *
     * @return A new {@code Timestamp} object with the current time.
     */
    public static Timestamp now() {
        Instant now = Instant.now();
        return new Timestamp(FORMATTER.format(now));
    }

    /**
     * Retrieves the Unix time (epoch seconds) of the timestamp.
     *
     * @return The Unix timestamp as a long value.
     */
    public long getUnixTime() {
        return unixTime;
    }

    /**
     * Returns a string representation of the timestamp, including both the
     * formatted timestamp and the Unix time.
     *
     * @return A formatted string representing the timestamp.
     */
    @Override
    public String toString() {
        return timestamp + " (Unix: " + unixTime + ")";
    }
}
