package telran.project.gardenshop.enums;

/**
 * Enum representing the time periods for grouping profit reports
 */
public enum GroupByPeriod {
    HOUR("HOUR"),
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH");

    private final String value;

    GroupByPeriod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Convert string to enum, case-insensitive
     * @param value the string value to convert
     * @return the corresponding enum value or null if not found
     */
    public static GroupByPeriod fromString(String value) {
        if (value == null) {
            return null;
        }
        
        for (GroupByPeriod period : GroupByPeriod.values()) {
            if (period.value.equalsIgnoreCase(value)) {
                return period;
            }
        }
        return null;
    }

    /**
     * Check if a string value is a valid group by period
     * @param value the string value to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String value) {
        return fromString(value) != null;
    }
} 