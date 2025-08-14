package telran.project.gardenshop.enums;

import org.junit.jupiter.api.Test;
import telran.project.gardenshop.exception.InvalidGroupByPeriodException;

import static org.junit.jupiter.api.Assertions.*;

class GroupByPeriodTest {

    @Test
    void fromString_WithValidValues_ShouldReturnCorrectEnum() {
        // Given & When & Then
        assertEquals(GroupByPeriod.HOUR, GroupByPeriod.fromString("HOUR"));
        assertEquals(GroupByPeriod.HOUR, GroupByPeriod.fromString("hour"));
        assertEquals(GroupByPeriod.HOUR, GroupByPeriod.fromString(" Hour "));

        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString("DAY"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString("day"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString(" Day "));

        assertEquals(GroupByPeriod.WEEK, GroupByPeriod.fromString("WEEK"));
        assertEquals(GroupByPeriod.WEEK, GroupByPeriod.fromString("week"));
        assertEquals(GroupByPeriod.WEEK, GroupByPeriod.fromString(" Week "));

        assertEquals(GroupByPeriod.MONTH, GroupByPeriod.fromString("MONTH"));
        assertEquals(GroupByPeriod.MONTH, GroupByPeriod.fromString("month"));
        assertEquals(GroupByPeriod.MONTH, GroupByPeriod.fromString(" Month "));
    }

    @Test
    void fromString_WithNullValue_ShouldThrowException() {
        // Given & When & Then
        InvalidGroupByPeriodException exception = assertThrows(
                InvalidGroupByPeriodException.class,
                () -> GroupByPeriod.fromString(null));

        assertEquals("Group by period cannot be null or empty", exception.getMessage());
    }

    @Test
    void fromString_WithEmptyValue_ShouldThrowException() {
        // Given & When & Then
        InvalidGroupByPeriodException exception = assertThrows(
                InvalidGroupByPeriodException.class,
                () -> GroupByPeriod.fromString(""));

        assertEquals("Group by period cannot be null or empty", exception.getMessage());
    }

    @Test
    void fromString_WithBlankValue_ShouldThrowException() {
        // Given & When & Then
        InvalidGroupByPeriodException exception = assertThrows(
                InvalidGroupByPeriodException.class,
                () -> GroupByPeriod.fromString("   "));

        assertEquals("Group by period cannot be null or empty", exception.getMessage());
    }

    @Test
    void fromString_WithInvalidValue_ShouldThrowException() {
        // Given & When & Then
        InvalidGroupByPeriodException exception = assertThrows(
                InvalidGroupByPeriodException.class,
                () -> GroupByPeriod.fromString("INVALID"));

        assertTrue(exception.getMessage().contains("Invalid group by period: 'INVALID'"));
        assertTrue(exception.getMessage().contains("Valid values are: HOUR, DAY, WEEK, MONTH"));
    }

    @Test
    void getValue_ShouldReturnCorrectString() {
        // Given & When & Then
        assertEquals("HOUR", GroupByPeriod.HOUR.getValue());
        assertEquals("DAY", GroupByPeriod.DAY.getValue());
        assertEquals("WEEK", GroupByPeriod.WEEK.getValue());
        assertEquals("MONTH", GroupByPeriod.MONTH.getValue());
    }

    @Test
    void fromString_WithDefaultValue_ShouldReturnDay() {
        // Given & When & Then
        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString("DAY"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString("day"));
        assertEquals(GroupByPeriod.DAY, GroupByPeriod.fromString(" Day "));
    }
}
