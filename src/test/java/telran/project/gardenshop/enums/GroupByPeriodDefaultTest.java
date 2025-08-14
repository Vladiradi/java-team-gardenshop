package telran.project.gardenshop.enums;

import org.junit.jupiter.api.Test;
import telran.project.gardenshop.utilities.GroupByPeriodUtils;
import static org.junit.jupiter.api.Assertions.*;

class GroupByPeriodDefaultTest {

    @Test
    void demonstrateDefaultBehavior() {
        // Тестируем новое поведение: null/пустые значения возвращают DAY

        // Null значение → DAY
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(null));

        // Пустая строка → DAY
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(""));

        // Строка только с пробелами → DAY
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("   "));

        // Валидные значения работают как раньше
        assertEquals(GroupByPeriod.HOUR, GroupByPeriodUtils.fromString("HOUR"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("DAY"));
        assertEquals(GroupByPeriod.WEEK, GroupByPeriodUtils.fromString("WEEK"));
        assertEquals(GroupByPeriod.MONTH, GroupByPeriodUtils.fromString("MONTH"));

        // Нечувствительность к регистру
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("day"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("Day"));

        // Убирание пробелов
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(" DAY "));

        System.out.println("✅ Все тесты прошли успешно!");
        System.out.println("📝 Новое поведение: null/пустые значения теперь возвращают DAY вместо исключения");
    }
}
