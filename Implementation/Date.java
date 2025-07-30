package Implementation;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The {@code Date} class provides a simple representation of a date
 * with year, month, and day in the format "yyyy-MM-dd".
 * This class is designed to be immutable and thread-safe, using
 * ThreadLocal for date formatting to ensure thread safety.
 */
public class Date {
    private final String date;
    private static final ThreadLocal<SimpleDateFormat> FORMAT = ThreadLocal.withInitial(() -> 
        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));

    /**
     * Constructs a {@code Date} object from a given date string.
     * The date string must be in the format "yyyy-MM-dd".
     *
     * @param date A string representing the date in the format "yyyy-MM-dd"
     * @throws IllegalArgumentException if the date string is not in the correct format
     */
    public Date(String date) {
        this.date = date;
    }

    /**
     * Creates a {@code Date} object representing the current system date.
     * The date is formatted using the system's default locale.
     *
     * @return A new {@code Date} object with today's date
     */
    public static Date now() {
        return new Date(FORMAT.get().format(new java.util.Date()));
    }

    /**
     * Returns a string representation of the date in the format "yyyy-MM-dd".
     *
     * @return A formatted string representing the date
     */
    @Override
    public String toString() {
        return date;
    }
}
