package telran.project.gardenshop.enums;

import lombok.Getter;
import telran.project.gardenshop.exception.InvalidGroupByPeriodException;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum GroupByPeriod {
    HOUR("HOUR"),
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH");

    private final String value;

    GroupByPeriod(String value) {
        this.value = value;
    }

    public static GroupByPeriod fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidGroupByPeriodException("Group by period cannot be null or empty");
        }

        for (GroupByPeriod period : GroupByPeriod.values()) {
            if (period.value.equalsIgnoreCase(value.trim())) {
                return period;
            }
        }

        String validValues = Arrays.stream(GroupByPeriod.values())
                .map(GroupByPeriod::getValue)
                .collect(Collectors.joining(", "));

        throw new InvalidGroupByPeriodException(value, validValues);
    }
}
