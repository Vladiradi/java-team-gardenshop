package telran.project.gardenshop.enums;

import lombok.Getter;

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

}
