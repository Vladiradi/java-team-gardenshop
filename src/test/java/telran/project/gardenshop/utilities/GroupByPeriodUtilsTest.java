package telran.project.gardenshop.utilities;

import org.junit.jupiter.api.Test;
import telran.project.gardenshop.enums.GroupByPeriod;
import telran.project.gardenshop.exception.InvalidGroupByPeriodException;

import static org.junit.jupiter.api.Assertions.*;

class GroupByPeriodUtilsTest {

    @Test
    void fromString_WithValidValues_ShouldReturnCorrectEnum() {
        // Given & When & Then
        assertEquals(GroupByPeriod.HOUR, GroupByPeriodUtils.fromString("HOUR"));
        assertEquals(GroupByPeriod.HOUR, GroupByPeriodUtils.fromString("hour"));
        assertEquals(GroupByPeriod.HOUR, GroupByPeriodUtils.fromString(" Hour "));

        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("DAY"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("day"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(" Day "));

        assertEquals(GroupByPeriod.WEEK, GroupByPeriodUtils.fromString("WEEK"));
        assertEquals(GroupByPeriod.WEEK, GroupByPeriodUtils.fromString("week"));
        assertEquals(GroupByPeriod.WEEK, GroupByPeriodUtils.fromString(" Week "));

        assertEquals(GroupByPeriod.MONTH, GroupByPeriodUtils.fromString("MONTH"));
        assertEquals(GroupByPeriod.MONTH, GroupByPeriodUtils.fromString("month"));
        assertEquals(GroupByPeriod.MONTH, GroupByPeriodUtils.fromString(" Month "));
    }

    @Test
    void fromString_WithNullOrEmptyValues_ShouldReturnDayAsDefault() {
        // Given & When & Then
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(null));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString(""));
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("   "));
    }

    @Test
    void fromString_WithInvalidValue_ShouldThrowException() {
        // Given & When & Then
        InvalidGroupByPeriodException exception = assertThrows(
                InvalidGroupByPeriodException.class,
                () -> GroupByPeriodUtils.fromString("INVALID"));

        assertTrue(exception.getMessage().contains("Invalid group by period: 'INVALID'"));
        assertTrue(exception.getMessage().contains("Valid values are: HOUR, DAY, WEEK, MONTH"));
    }

    @Test
    void isValid_WithValidValues_ShouldReturnTrue() {
        // Given & When & Then
        assertTrue(GroupByPeriodUtils.isValid("HOUR"));
        assertTrue(GroupByPeriodUtils.isValid("hour"));
        assertTrue(GroupByPeriodUtils.isValid(" Day "));
        assertTrue(GroupByPeriodUtils.isValid("WEEK"));
        assertTrue(GroupByPeriodUtils.isValid("MONTH"));
    }

    @Test
    void isValid_WithNullOrEmptyValues_ShouldReturnTrue() {
        // Given & When & Then
        assertTrue(GroupByPeriodUtils.isValid(null));
        assertTrue(GroupByPeriodUtils.isValid(""));
        assertTrue(GroupByPeriodUtils.isValid("   "));
    }

    @Test
    void isValid_WithInvalidValues_ShouldReturnFalse() {
        // Given & When & Then
        assertFalse(GroupByPeriodUtils.isValid("INVALID"));
        assertFalse(GroupByPeriodUtils.isValid("YEARS"));
        assertFalse(GroupByPeriodUtils.isValid("SECONDS"));
    }

    @Test
    void getValidValues_ShouldReturnAllEnumValues() {
        // Given & When
        String validValues = GroupByPeriodUtils.getValidValues();

        // Then
        assertEquals("HOUR, DAY, WEEK, MONTH", validValues);
    }

    @Test
    void demonstrateCleanArchitecture() {
        // Демонстрируем чистую архитектуру

        // 1. Enum содержит только значения (без логики)
        assertEquals("HOUR", GroupByPeriod.HOUR.name());
        assertEquals("DAY", GroupByPeriod.DAY.name());
        assertEquals("WEEK", GroupByPeriod.WEEK.name());
        assertEquals("MONTH", GroupByPeriod.MONTH.name());

        // 2. Вся логика находится в утилитном классе
        assertEquals(GroupByPeriod.DAY, GroupByPeriodUtils.fromString("day"));
        assertTrue(GroupByPeriodUtils.isValid("HOUR"));
        assertEquals("HOUR, DAY, WEEK, MONTH", GroupByPeriodUtils.getValidValues());

        System.out.println("✅ Чистая архитектура: enum содержит только значения, логика в утилитном классе!");
    }
}
