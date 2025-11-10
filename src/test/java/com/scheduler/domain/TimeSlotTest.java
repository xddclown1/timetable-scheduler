package com.scheduler.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {

    @Test
    void shouldCreateValidTimeSlot() {
        TimeSlot slot = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        assertEquals(1, slot.getSlotIndex());
        assertEquals(DayOfWeek.MONDAY, slot.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), slot.getStartTime());
        assertEquals(LocalTime.of(10, 0), slot.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenEndTimeBeforeStartTime() {
        assertThrows(IllegalArgumentException.class, () ->
            TimeSlot.builder()
                .slotIndex(1)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(9, 0))
                .build()
        );
    }

    @Test
    void shouldThrowExceptionWhenEndTimeEqualsStartTime() {
        assertThrows(IllegalArgumentException.class, () ->
            TimeSlot.builder()
                .slotIndex(1)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 0))
                .build()
        );
    }

    @Test
    void shouldDetectOverlappingTimeSlots() {
        TimeSlot slot1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 30))
            .endTime(LocalTime.of(10, 30))
            .build();

        assertTrue(slot1.overlapsWith(slot2));
        assertTrue(slot2.overlapsWith(slot1));
    }

    @Test
    void shouldNotDetectOverlapOnDifferentDays() {
        TimeSlot slot1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.TUESDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        assertFalse(slot1.overlapsWith(slot2));
    }

    @Test
    void shouldDetectConsecutiveTimeSlots() {
        TimeSlot slot1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build();

        assertTrue(slot1.isConsecutiveWith(slot2));
        assertFalse(slot2.isConsecutiveWith(slot1));
    }

    @Test
    void shouldImplementEqualsAndHashCodeCorrectly() {
        TimeSlot slot1 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot slot2 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        assertEquals(slot1, slot2);
        assertEquals(slot1.hashCode(), slot2.hashCode());
    }

    @Test
    void shouldCompareTimeSlotsCorrectly() {
        TimeSlot monday9 = TimeSlot.builder()
            .slotIndex(1)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        TimeSlot monday10 = TimeSlot.builder()
            .slotIndex(2)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .build();

        TimeSlot tuesday9 = TimeSlot.builder()
            .slotIndex(3)
            .dayOfWeek(DayOfWeek.TUESDAY)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .build();

        assertTrue(monday9.compareTo(monday10) < 0);
        assertTrue(monday10.compareTo(monday9) > 0);
        assertTrue(monday9.compareTo(tuesday9) < 0);
    }
}