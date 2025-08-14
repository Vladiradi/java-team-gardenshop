package telran.project.gardenshop.utilities;

import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.exception.InvalidGroupByPeriodException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GroupByPeriodUtils {

    /**
     * Преобразует строку в GroupByPeriod enum.
     * Если значение null или пустое, возвращает DAY по умолчанию.
     * Если значение неверное, выбрасывает исключение.
     *
     * @param value строка для преобразования
     * @return соответствующий GroupByPeriod enum
     * @throws InvalidGroupByPeriodException если значение неверное
     */
    public static GroupByPeriod fromString(String value) {
        // Если значение null или пустое, возвращаем DAY по умолчанию
        if (value == null || value.trim().isEmpty()) {
            return GroupByPeriod.DAY;
        }

        for (GroupByPeriod period : GroupByPeriod.values()) {
            if (period.name().equalsIgnoreCase(value.trim())) {
                return period;
            }
        }

        String validValues = Arrays.stream(GroupByPeriod.values())
                .map(GroupByPeriod::name)
                .collect(Collectors.joining(", "));

        throw new InvalidGroupByPeriodException(value, validValues);
    }

    /**
     * Проверяет, является ли строка валидным значением GroupByPeriod.
     *
     * @param value строка для проверки
     * @return true если значение валидное, false в противном случае
     */
    public static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true; // null/пустые значения считаются валидными (используется DAY по умолчанию)
        }

        for (GroupByPeriod period : GroupByPeriod.values()) {
            if (period.name().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает список всех допустимых значений GroupByPeriod.
     *
     * @return строка с перечислением допустимых значений
     */
    public static String getValidValues() {
        return Arrays.stream(GroupByPeriod.values())
                .map(GroupByPeriod::name)
                .collect(Collectors.joining(", "));
    }
}
