package foundation.stack.datamill.values;

import java.time.*;
import java.time.temporal.Temporal;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public final class Times {
    private Times() {
    }

    public static long toEpochMillis(Temporal temporal) {
        if (temporal instanceof LocalDate) {
            return ((LocalDate) temporal).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant().toEpochMilli();
        } else if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant().toEpochMilli();
        }

        throw new IllegalArgumentException("The specified temporal type is not supported!");
    }
}
